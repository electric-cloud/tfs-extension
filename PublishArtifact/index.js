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
var efEndpoint = tl.getInput(ENDPOINT_FIELD, true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
var skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], skipCertCheck);
var artifactName = tl.getInput(ARTIFACT_NAME_FIELD);
var artifactPath = tl.getInput(ARTIFACT_PATH_FIELD);
var repositoryName = tl.getInput(REPO_NAME_FIELD);
var artifactVersion = tl.getInput(ARTIFACT_VERSION_FIELD);
if (!fs.existsSync(artifactPath)) {
    tl.setResult(tl.TaskResult.Failed, "File " + artifactPath + " does not exist");
}
else {
    efClient.login().then(function (res) {
        var sid = res.sessionId;
        return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
    }).then(function (res) {
        if (res.response == "Artifact-Published-OK") {
            console.log("Artifact published");
            tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
        }
        else {
            tl.setResult(tl.TaskResult.Failed, res.response);
        }
    }).catch(function (e) {
        tl.setResult(tl.TaskResult.Failed, e);
    });
}
