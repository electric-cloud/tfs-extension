"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const tl = require("vsts-task-lib/task");
const ef_client_1 = require("ef-client");
let parseParameters = function (params) {
    let retval = {};
    try {
        retval = JSON.parse(params);
    }
    catch (e) {
        let lines = params.split(/\n/);
        for (let i = 0; i < lines.length; i++) {
            let line = lines[i];
            let pair = line.split(/\s*=\s*/);
            let key = pair[0];
            let value = pair[1];
            retval[key] = value;
        }
    }
    return retval;
};
var efEndpoint = tl.getInput('electricFlowService', true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
var requiresAdditionalParameters = tl.getBoolInput('requiresAdditionalParameters', false);
var additionalParamsString = tl.getInput("additionalParameters");
if (efAuth.parameters['skipCertCheck'] == 'true') {
    console.log("Certificate check is skipped");
}
let skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], efAuth.parameters['restVersion'], skipCertCheck);
let projectName = tl.getInput('projectName');
let pipelineName = tl.getInput('pipelineName');
let pipelinePromise = efClient.getProject(projectName).then((res) => {
    return efClient.getPipeline(pipelineName, projectName);
});
pipelinePromise.then((res) => {
    if (requiresAdditionalParameters) {
        let additionalParams = parseParameters(additionalParamsString);
        return efClient.runPipelineWithParameters(pipelineName, projectName, additionalParams);
    }
    else {
        return efClient.runPipeline(pipelineName, projectName);
    }
}).then((res) => {
    let runtimeName = res.flowRuntime.flowRuntimeName;
    console.log("Pipeline run succeeded, runtime name is " + runtimeName);
    tl.setResult(tl.TaskResult.Succeeded, "Successfully run pipeline " + pipelineName);
}).catch((e) => {
    console.log(e);
    let message = 'Cannot run pipeline';
    if (e.response && e.response.error) {
        message = e.response.error.message;
    }
    tl.setResult(tl.TaskResult.Failed, message);
});
//# sourceMappingURL=index.js.map