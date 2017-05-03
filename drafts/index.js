"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var ef_client_1 = require("ef-client");
var artifactPath = '/tmp/artifact';
var artifactName = 'org.mycompany:artifact';
var repositoryName = 'default';
var artifactVersion = '3';
var efClient = new ef_client_1.EFClient('https://rhel-oracle', 'admin', 'changeme', true);
efClient.login().then(function (res) {
    var sid = res.sessionId;
    return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
}).then(function (res) {
    if (res.response == "Artifact-Published-OK") {
        console.log("Artifact published");
        // tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
    }
    else {
        console.log("Publish failed: " + res.response);
        // tl.setResult(tl.TaskResult.Failed, res.response);
    }
}).catch(function (e) {
    console.log("Error occured: " + e);
    // tl.setResult(tl.TaskResult.Failed, e);
});
