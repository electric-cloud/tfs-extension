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
let pipelineName = tl.getInput('pipelineName');

let pipelinePromise = efClient.getProject(projectName).then((res) => {
    return efClient.getPipeline(pipelineName, projectName);
});

let pipelineLink = '';

pipelinePromise.then((res: any) => {
    if (requiresAdditionalParameters) {
        try {
            let additionalParams = efClient.parseParameters(additionalParamsString);
            return efClient.runPipelineWithParameters(pipelineName, projectName, additionalParams);
        } catch(e) {
            tl.setResult(tl.TaskResult.Failed, e);
        }
    }
    else {
        return efClient.runPipeline(pipelineName, projectName);
    }
}).then((res: any) => {
    let flowRuntimeId = res.flowRuntime.flowRuntimeId;
    let runtimeName = res.flowRuntime.flowRuntimeName;
    let pipelineId = res.flowRuntime.pipelineId;
    let link = efClient.createFlowRuntimeLink(efBaseUrl, pipelineId, flowRuntimeId);
    console.log("Pipeline run succeeded, runtime name is " + runtimeName);
    console.log("Link to the pipeline runtime: " + link);
    tl.setResult(tl.TaskResult.Succeeded, "Successfully run pipeline " + pipelineName + ', link to the pipeline: ' + link);
}).catch((e) => {
    console.log(e);
    let message = 'Cannot run pipeline';
    if (e.response && e.response.error) {
        message = e.response.error.message;
    }
    tl.setResult(tl.TaskResult.Failed, message);
});



