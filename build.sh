#!/usr/bin/env bash

if [[ $TFS_COMPILE = "1" ]]
then
    for FOLDER_NAME in 'ef-client' 'RunPipeline' 'PublishArtifact' 'RESTCall'
    do
        cd $FOLDER_NAME
        rm -rf node_modules
        npm install
        tsc
        cd ..
    done
fi

perl increaseVersion.pl

TFS_SERVERNAME=http://10.200.1.158:8080/tfs
TFS_PAT=vx6e2fr2vb3esw6hl55zpnlghtxiw5kavtgh3u7fmycpundd3oaa

if [[ $1 = "--local" ]]
then
    echo "Installing extension locally"
    tfx extension publish --token $TFS_PAT \
 --manifest-globs vss-extension.json --service-url $TFS_SERVERNAME

    rm -rf pluginsdev.electric-flow-*
else
    tfx extension create --manifest-globs vss-extension.json
fi
