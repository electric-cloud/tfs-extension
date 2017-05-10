"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ef_client_1 = require("ef-client");
const tl = require("vsts-task-lib/task");
const fs = require("fs");
const ENDPOINT_FIELD = 'electricFlowService';
const ARTIFACT_PATH_FIELD = 'artifactPath';
const REPO_NAME_FIELD = 'repositoryName';
const ARTIFACT_VERSION_FIELD = 'artifactVersion';
const ARTIFACT_NAME_FIELD = 'artifactName';
var efEndpoint = tl.getInput(ENDPOINT_FIELD, true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
let skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], efAuth.parameters['restVersion'], skipCertCheck);
let artifactName = tl.getInput(ARTIFACT_NAME_FIELD);
let artifactPath = tl.getInput(ARTIFACT_PATH_FIELD);
let repositoryName = tl.getInput(REPO_NAME_FIELD);
let artifactVersion = tl.getInput(ARTIFACT_VERSION_FIELD);
if (!fs.existsSync(artifactPath)) {
    tl.setResult(tl.TaskResult.Failed, "File " + artifactPath + " does not exist");
}
else {
    let sid = undefined;
    efClient.login().then((res) => {
        sid = res.sessionId;
        return efClient.getRepository(repositoryName);
    }).then((res) => {
        return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
    }).then((res) => {
        if (res.response == "Artifact-Published-OK") {
            console.log("Artifact published");
            tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
        }
        else {
            tl.setResult(tl.TaskResult.Failed, res.response);
        }
    }).catch((e) => {
        console.log(e);
        if (e.response) {
            let message = e.response.error ? e.response.error.message : 'Artifact publication failed';
            tl.setResult(tl.TaskResult.Failed, message);
        }
        else {
            tl.setResult(tl.TaskResult.Failed, "Artifact publication failed");
        }
    });
}
//# sourceMappingURL=index.js.map