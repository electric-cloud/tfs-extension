"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var ef_client_1 = require("ef-client");
var tl = require("vsts-task-lib/task");
var fs = require("fs");
var ENDPOINT_FIELD = 'electricFlowService';
var ARTIFACT_PATH_FIELD = 'artifactPath';
var REPO_NAME_FIELD = 'repositoryName';
var ARTIFACT_VERSION_FIELD = 'artifactVersion';
var ARTIFACT_NAME_FIELD = 'artifactName';
// var efEndpoint = tl.getInput(ENDPOINT_FIELD, true);
// var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var efBaseUrl = 'https://rhel-oracle/rest/v1.0';
// var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
// let skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';
var efAuth = { parameters: { username: 'admin1', password: 'changeme' } };
var skipCertCheck = true;
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], skipCertCheck);
// let artifactName = tl.getInput(ARTIFACT_NAME_FIELD);
// let artifactPath = tl.getInput(ARTIFACT_PATH_FIELD);
// let repositoryName = tl.getInput(REPO_NAME_FIELD);
// let artifactVersion = tl.getInput(ARTIFACT_VERSION_FIELD);
var artifactName = 'com.mycompany:artifact';
var artifactPath = '/tmp/artifact';
var repositoryName = 'default';
var artifactVersion = '1';
if (!fs.existsSync(artifactPath)) {
    tl.setResult(tl.TaskResult.Failed, "File " + artifactPath + " does not exist");
}
else {
    var sid_1 = undefined;
    efClient.login().then(function (res) {
        sid_1 = res.sessionId;
        return efClient.getRepository(repositoryName);
    }).then(function (res) {
        return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid_1);
    }).then(function (res) {
        if (res.response == "Artifact-Published-OK") {
            console.log("Artifact published");
            tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
        }
        else {
            tl.setResult(tl.TaskResult.Failed, res.response);
        }
    }).catch(function (e) {
        console.log(e);
        if (e.response) {
            var message = e.response.error ? e.response.error.message : 'Artifact publication failed';
            tl.setResult(tl.TaskResult.Failed, message);
        }
        else {
            tl.setResult(tl.TaskResult.Failed, "Artifact publication failed");
        }
    });
    // let publishArtifactPromise = repositoryPromise.then((res: any) => {
    //     return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
    // }).catch((e) => {
    //     if (e.statusCode == 404) {
    //     }
    // });
    // publishArtifactPromise.then((res: any) => {
    //     if (res.response == "Artifact-Published-OK") {
    //         console.log("Artifact published");
    //         tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
    //     }
    //     else {
    //         tl.setResult(tl.TaskResult.Failed, res.response);
    //     }
    // }).catch((e) => {
    //     console.log(e);
    //     tl.setResult(tl.TaskResult.Failed, "Publish artifact failed");
    // });
    // efClient.login().then((res: any) => {
    //     return efClient.getRepository(repositoryName);
    // }).then((res: any) => {
    //     let sid = res.sessionId;
    //     return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
    // }).then((res: any) => {
    //     if (res.response == "Artifact-Published-OK") {
    //         console.log("Artifact published");
    //         tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
    //     }
    //     else {
    //         tl.setResult(tl.TaskResult.Failed, res.response);
    //     }
    // }).catch((e) => {
    //     console.log(e);
    //     tl.setResult(tl.TaskResult.Failed, e);
    // });
}
