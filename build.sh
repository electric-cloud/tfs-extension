#!/usr/bin/env bash


for FOLDER_NAME in 'ef-client' 'RunPipeline' 'PublishArtifact' 'RESTCall'
do
    cd $FOLDER_NAME
    rm -rf node_modules
    npm install
    tsc
    cd ..
done

perl increaseVersion.pl

TFS_SERVERNAME=http://desktop-2760qqq:8080/tfs
TFS_PAT=lgnfxowe2tgr743y3w3qwhdvtcb2xxtyt6anrnkmhrsin5m3zklq

if [[ $1 = "--local" ]]
then
    echo "Installing extension locally"
    tfx extension publish --token $TFS_PAT \
 --manifest-globs vss-extension.json --service-url $TFS_SERVERNAME
else
    echo "Installing extension globally"

    ACCOUNT=pluginsdev
    PAT=$(cat ~/.tfs_pat)

    tfx extension publish --manifest-globs vss-extension.json \
--share-with $ACCOUNT --token $PAT
fi


rm -rf pluginsdev.electric-flow-0.1.*

