import tl = require('vsts-task-lib/task');
import url = require('url');

import {EFClient} from "ef-client";

var efEndpoint = tl.getInput('electricFlowService', true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var trustCerts = tl.getEndpointDataParameter(efEndpoint, 'acceptUntrustedCerts', true);
var restVersion = tl.getEndpointDataParameter(efEndpoint, 'restVersion', true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
var requiresAdditionalParameters = tl.getBoolInput('requiresAdditionalParameters', false);
var additionalParamsString = tl.getInput("additionalParameters");


if (trustCerts == 'true') {
    console.log("Certificate check is skipped");
}
let skipCertCheck = trustCerts == 'true';

var efClient = new EFClient(
    efBaseUrl,
    efAuth.parameters['username'],
    efAuth.parameters['password'],
    restVersion,
    skipCertCheck
);
let projectName = tl.getInput('projectName');
let releaseName = tl.getInput('releaseName');
let startingStageName = tl.getInput('startingStageName');
let stagesToRun = tl.getInput('stagesToRun');

let releasePromise = efClient.getProject(projectName).then((res) => {
    return efClient.getRelease(releaseName, projectName);
});

let releaseLink = '';

releasePromise.then((res: any) => {
    if (requiresAdditionalParameters) {
        let additionalParams = efClient.parseParameters(additionalParamsString);
        try {
            let additionalParams = efClient.parseParameters(additionalParamsString);
            return efClient.releaseWithParameters(projectName, releaseName, startingStageName, stagesToRun, additionalParams);
        } catch(e) {
            tl.setResult(tl.TaskResult.Failed, e);
        }
    }
    else {
        return efClient.release(projectName, releaseName, startingStageName, stagesToRun);
    }
}).then((res: any) => {
    let flowRuntimeId = res.flowRuntime.flowRuntimeId;
    let runtimeName = res.flowRuntime.flowRuntimeName;
    let pipelineId = res.flowRuntime.pipelineId;
    let link = efClient.createFlowRuntimeLink(efBaseUrl, pipelineId, flowRuntimeId);
    console.log("Release run succeeded, runtime name is " + runtimeName);
    console.log("Link to the release pipeline runtime: " + link);
    tl.setResult(tl.TaskResult.Succeeded, "Successfully trigger release " + releaseName + ', link to the pipeline: ' + link);
}).catch((e) => {
    console.log(e);
    let message = 'Cannot trigger release';
    if (e.response && e.response.error) {
        message = e.response.error.message;
    }
    tl.setResult(tl.TaskResult.Failed, message);
});
