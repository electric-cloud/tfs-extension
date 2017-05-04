"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var tl = require("vsts-task-lib/task");
var ef_client_1 = require("ef-client");
var parseParameters = function (params) {
    var retval = {};
    try {
        retval = JSON.parse(params);
    }
    catch (e) {
        // console.log(e);
        var lines = params.split("/n");
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            var pair = line.split(/\s*=\s*/);
            var key = pair[0];
            var value = pair[1];
            retval[key] = value;
        }
    }
    console.log(retval);
    return retval;
};
// var efEndpoint = tl.getInput('electricFlowService', true);
// var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
// var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
var requiresAdditionalParameters = tl.getBoolInput('requiresAdditionalParameters', false);
requiresAdditionalParameters = true;
var additionalParamsString = tl.getInput("additionalParameters");
additionalParamsString = 'name=value';
var efBaseUrl = 'http://rhel-oracle';
var efAuth = { parameters: { username: 'admin', password: 'changeme', skipCertCheck: true } };
// if (efAuth.parameters['skipCertCheck'] == 'true') {
//     console.log("Certificate check is skipped");
// }
var skipCertCheck = true;
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], skipCertCheck);
var projectName = tl.getInput('projectName');
projectName = 'Default';
var pipelineName = tl.getInput('pipelineName');
pipelineName = 'Test TFS With Parameters';
var pipelinePromise = efClient.getProject(projectName).then(function (res) {
    return efClient.getPipeline(pipelineName, projectName);
});
pipelinePromise.then(function (res) {
    if (requiresAdditionalParameters) {
        var additionalParams = parseParameters(additionalParamsString);
        return efClient.runPipelineWithParameters(pipelineName, projectName, additionalParams);
    }
    else {
        return efClient.runPipeline(pipelineName, projectName);
    }
}).then(function (res) {
    var runtimeName = res.flowRuntime.flowRuntimeName;
    console.log("Pipeline run succeeded, runtime name is " + runtimeName);
    tl.setResult(tl.TaskResult.Succeeded, "Successfully run pipeline " + pipelineName);
}).catch(function (e) {
    console.log(e);
    tl.setResult(tl.TaskResult.Failed, e);
});
