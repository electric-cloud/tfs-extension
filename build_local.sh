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

cd RESTCall
rm -rf node_modules
npm install
tsc
cd ..

perl increaseVersion.pl

account=pluginsdev
token=$(cat ~/.tfs_pat)

tfx extension publish --token lgnfxowe2tgr743y3w3qwhdvtcb2xxtyt6anrnkmhrsin5m3zklq \
 --manifest-globs vss-extension.json --service-url http://desktop-2760qqq:8080/tfs



