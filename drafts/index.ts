import {EFClient} from "ef-client";

import fs = require('fs');
import process = require('process');
import https = require('https');
import q = require('q');
import FormData = require('form-data');
// import {BufferConcat} from 'buffer-concat';
import {Buffer} from 'buffer';



let artifactPath = '/tmp/artifact';
let artifactName = 'org.mycompany:artifact';
let repositoryName = 'default';
let artifactVersion = '3';

let efClient = new EFClient('https://rhel-oracle', 'admin', 'changeme', true);

efClient.login().then((res: any) => {
    let sid = res.sessionId;
    return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
}).then((res: any) => {
    if (res.response == "Artifact-Published-OK") {
        console.log("Artifact published");

        // tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
    }
    else {
        console.log(`Publish failed: ${res.response}`);
        // tl.setResult(tl.TaskResult.Failed, res.response);
    }
}).catch((e) => {
    console.log(`Error occured: ${e}`);
    // tl.setResult(tl.TaskResult.Failed, e);
});
