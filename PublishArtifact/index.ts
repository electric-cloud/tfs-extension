import {EFClient} from 'ef-client';
import tl = require('vsts-task-lib/task');
import fs = require('fs');

const ENDPOINT_FIELD = 'electricFlowService';
const ARTIFACT_PATH_FIELD = 'artifactPath';
const REPO_NAME_FIELD = 'repositoryName';
const ARTIFACT_VERSION_FIELD = 'artifactVersion';
const ARTIFACT_NAME_FIELD = 'artifactName';

var efEndpoint = tl.getInput(ENDPOINT_FIELD, true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
let skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';

var efClient = new EFClient(
    efBaseUrl,
    efAuth.parameters['username'],
    efAuth.parameters['password'],
    skipCertCheck
);

let artifactName = tl.getInput(ARTIFACT_NAME_FIELD);
let artifactPath = tl.getInput(ARTIFACT_PATH_FIELD);
let repositoryName = tl.getInput(REPO_NAME_FIELD);
let artifactVersion = tl.getInput(ARTIFACT_VERSION_FIELD);

if(!fs.existsSync(artifactPath)) {
    tl.setResult(tl.TaskResult.Failed, "File " + artifactPath + " does not exist");
}
else {
    efClient.login().then((res: any) => {
        let sid = res.sessionId;
        return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
    }).then((res: any) => {
        if (res.response == "Artifact-Published-OK") {
            console.log("Artifact published");
            tl.setResult(tl.TaskResult.Succeeded, "Successfully published artifact " + artifactName);
        }
        else {
            tl.setResult(tl.TaskResult.Failed, res.response);
        }
    }).catch((e) => {
        tl.setResult(tl.TaskResult.Failed, e);
    });
}
