use strict;
use warnings;
use JSON;
use Data::Dumper;
use Getopt::Long;
use File::Path qw(rmtree);

my ($local, $increase_patch, $compile);
GetOptions('local' => \$local, 'patch' => \$increase_patch, 'compile' => \$compile) or die;

if ($compile) {
    my @tasks = qw(ef-client RunPipeline PublishArtifact RESTCall TriggerRelease);
    for my $folder (@tasks) {
        rmtree("$folder/node_modules");
        chdir($folder);
        my $out = `npm install 2>&1`;
        print $out;

        $out = `tsc`;
        print $out;
        chdir('..');
    }
}

increase_version();

if ($local) {
    `rm -rf pluginsdev.electric-flow*`;
    open my $fh, './.tfs' or die "Cannot open .tfs: $!";
    my ($servername, $token) = <$fh>;
    close $fh;
    chomp $servername;
    chomp $token;
    # my $token = 'yb4lp3cnxzx2gqykokji5lrgammvwjc2ntywgeoi2bms7ff3n4za';
    # my $servername = 'http://10.200.1.56:8080/tfs';
    my $out = `tfx extension publish --token $token --manifest-globs vss-extension.json --service-url $servername 1>&2`;
    print $out;
}
else {
    `rm -rf pluginsdev.electric-flow*`;
    my $out = `tfx extension create --manifest-globs vss-extension.json`;
    print $out;
}

sub increase_version {
    open my $fh, 'vss-extension.json' or die $!;
    my $content = join '' => <$fh>;
    close $fh;

    my ($major, $minor, $patch, $build) = $content =~ m/"version": "(\d+)\.(\d+)\.(\d+).(\d+)/sm;
    my $new_build = $ENV{BUILD_NUMBER} || $build + 1;
    if ($increase_patch) {
        $patch ++;
    }

    $content =~ s/"version": "(\d+)\.(\d+)\.(\d+).(\d+)"/"version": "$major.$minor.$patch.$new_build"/;
    open $fh, ">vss-extension.json" or die $!;
    print $fh $content;
    close $fh;

    my $vss_extension = decode_json($content);
    my @tasks = grep {$_->{type} eq 'ms.vss-distributed-task.task'} @{$vss_extension->{contributions}};

    my @folders = map {$_->{properties}->{name}} @tasks;
    for (@folders) {
        build_task($_, $major, $minor, $patch);
    }
}


sub build_task {
    my ($folder, $major, $minor, $patch) = @_;

    return unless -f "./$folder/task.json";
    open my $fh, "./$folder/task.json" or die $!;
    my $content = join '' => <$fh>;
    close $fh;

    my $task;
    eval {
        $task = decode_json($content);
        1;
    } or do {
        die "Cannot decode json file $folder: $@\n";
    };
    $task->{version}->{Patch} = $patch;
    $task->{version}->{Major} = $major;
    $task->{version}->{Minor} = $minor;

    # $content =~ s/"Patch": "$build"/"Patch": "$new_build"/sm;
    open $fh, ">./$folder/task.json" or die $!;
    print $fh JSON->new->utf8->pretty->encode($task);
    close $fh;

    print "Folder $folder done\n";
}
