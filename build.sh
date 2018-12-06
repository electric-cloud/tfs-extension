#!/usr/bin/env bash

if [[ $TFS_COMPILE = "1" ]]
then
    for FOLDER_NAME in 'ef-client' 'RunPipeline' 'PublishArtifact' 'RESTCall' 'TriggerRelease'
    do
        cd $FOLDER_NAME
        rm -rf node_modules
        npm install
        tsc
        cd ..
    done
fi

perl increaseVersion.pl

TFS_SERVERNAME=http://10.200.1.220:8080/tfs
TFS_PAT=yqc7wyi5gdtgqkgpo7wiv52an4ejjhzv42pyib4wpjvx52z7vi6a

if [[ $1 = "--local" ]]
then
    echo "Installing extension locally"
    tfx extension publish --token $TFS_PAT \
 --manifest-globs vss-extension.json --service-url $TFS_SERVERNAME

    rm -rf pluginsdev.electric-flow-*
else
    tfx extension create --manifest-globs vss-extension.json
fi
