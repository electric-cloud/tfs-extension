use strict;
use warnings;
use JSON;
use Data::Dumper;
use Getopt::Long;

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


sub build_task {
    my ($folder) = @_;

    return unless -f "./$folder/task.json";
    open $fh, "./$folder/task.json" or die $!;
    $content = join '' => <$fh>;
    close $fh;

    my $task;
    eval {
        $task = decode_json($content);
        1;
    } or do {
        die "Cannot decode json file $folder: $@\n";
    };
    $task->{version}->{Patch} = $new_build;

    # $content =~ s/"Patch": "$build"/"Patch": "$new_build"/sm;
    open $fh, ">./$folder/task.json" or die $!;
    print $fh JSON->new->utf8->pretty->encode($task);
    close $fh;

    print "Folder $folder done\n";
}
