#!/usr/bin/env bash

tsc ef-client/index.ts

rm -rf RunPipeline/node_modules
cd RunPipeline
npm install
tsc
cd ..


cd PublishArtifact
rm -rf node_modules
npm install
tsc
cd ..

perl increaseVersion.pl

account=pluginsdev
token=$(cat ~/.tfs_pat)

echo  $account
echo $token

tfx extension publish --manifest-globs vss-extension.json \
--share-with $account --token $token

