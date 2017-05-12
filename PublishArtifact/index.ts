import {EFClient} from 'ef-client';
import tl = require('vsts-task-lib/task');
import fs = require('fs');
import querystring = require('querystring');


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
    efAuth.parameters['restVersion'],
    skipCertCheck
);

let artifactName = tl.getInput(ARTIFACT_NAME_FIELD);
let artifactPath = tl.getInput(ARTIFACT_PATH_FIELD);
let repositoryName = tl.getInput(REPO_NAME_FIELD);
let artifactVersion = tl.getInput(ARTIFACT_VERSION_FIELD);

let createArtifactLink = function(endpoint: string, artifactName: string, artifactVersion: string) {
    let escapedName = querystring.escape(artifactName);
    if (endpoint.match(/\/$/)) {
        endpoint = endpoint.replace(/\/$/, '');
    }
    let url = endpoint + '/commander/link/artifactVersionDetails/artifactVersions/' + escapedName + '%3A' + artifactVersion + '?s=Artifacts&ss=Artifact%20Versions';
    return url;
}


if(!fs.existsSync(artifactPath)) {
    tl.setResult(tl.TaskResult.Failed, "File " + artifactPath + " does not exist");
}
else {
    let sid = undefined;
    efClient.login().then((res:any) => {
        sid = res.sessionId;
        return efClient.getRepository(repositoryName);
    }).then((res: any) => {
        return efClient.publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, sid);
    }).then((res: any) => {
        if (res.response == "Artifact-Published-OK") {
            console.log("Artifact published");
            let link = createArtifactLink(efBaseUrl, artifactName, artifactVersion);
            console.log("Link to the artifact: " + link);
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
            tl.setResult(tl.TaskResult.Failed, "Artifact publication failed")
        }
    });
}
