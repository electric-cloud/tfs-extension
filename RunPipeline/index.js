"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const tl = require("vsts-task-lib/task");
const ef_client_1 = require("ef-client");
var efEndpoint = tl.getInput('electricFlowService', true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
console.log("EF Base URL: ", efBaseUrl);
tl.debug(efBaseUrl);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
console.log("EF auth: ", efAuth);
console.log(efAuth.parameters);
if (efAuth.parameters['skipCertCheck'] == 'true') {
    console.log("Certificate check is skipped");
}
let skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], skipCertCheck);
let projectName = tl.getInput('projectName');
let pipelineName = tl.getInput('pipelineName');
tl.debug("Project name: " + projectName + ", pipelineName: " + pipelineName);
let pipelinePromise = efClient.getProject(projectName).then((res) => {
    return efClient.getPipeline(pipelineName, projectName);
});
pipelinePromise.then((res) => {
    return efClient.runPipeline(pipelineName, projectName);
}).then((res) => {
    console.log("Pipeline run succeeded");
    console.log(res);
    tl.setResult(tl.TaskResult.Succeeded, "Successfully run pipeline " + pipelineName);
}).catch((e) => {
    console.log(e);
    tl.setResult(tl.TaskResult.Failed, e);
});
//# sourceMappingURL=index.js.map