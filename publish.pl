use strict;
use warnings;
use JSON;
use Data::Dumper;


open my $fh, 'vss-extension.json' or die $!;
my $content = join '' => <$fh>;
close $fh;

my ($major, $minor, $build) = $content =~ m/"version": "(\d+)\.(\d+)\.(\d+)/sm;
my $new_build = $build + 1;

$content =~ s/"version": "(\d+)\.(\d+)\.(\d+)"/"version": "$major.$minor.$new_build"/;
open $fh, ">vss-extension.json" or die $!;
print $fh $content;
close $fh;


my $vss_extension = decode_json($content);
my @tasks = grep {$_->{type} eq 'ms.vss-distributed-task.task'} @{$vss_extension->{contributions}};

my @folders = map {$_->{properties}->{name}} @tasks;
for (@folders) {
    build_task($_);
}

my $account = 'pluginsdev';

open $fh, $ENV{HOME} .'/.tfs_pat' or die $!;
my $token = <$fh>;

close $fh;


my $output = `tfx extension publish --manifest-globs vss-extension.json --share-with $account --token $token`;
print $output;


sub build_task {
    my ($folder) = @_;

    return unless -f "./$folder/task.json";
    open $fh, "./$folder/task.json" or die $!;
    $content = join '' => <$fh>;
    close $fh;

    my $task = decode_json($content);
    $task->{version}->{Patch} = $new_build;

    # $content =~ s/"Patch": "$build"/"Patch": "$new_build"/sm;
    open $fh, ">./$folder/task.json" or die $!;
    print $fh JSON->new->utf8->pretty->canonical->encode($task);
    close $fh;

    print "Folder $folder done\n";
}
