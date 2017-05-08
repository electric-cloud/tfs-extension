"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var ef_client_1 = require("ef-client");
var tl = require("vsts-task-lib/task");
var efEndpoint = tl.getInput('electricFlowService', true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
var skipCertCheck = efAuth.parameters['skipCertCheck'] == 'true';
if (skipCertCheck) {
    console.log("Certificate check is skipped");
}
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], skipCertCheck);
var method = tl.getInput('method', true);
var paramsString = tl.getInput('params', false);
var restEndpoint = tl.getInput('restEndpoint', true);
var resVarName = tl.getInput('resultVarName', true);
var payload = tl.getInput('payload', false);
var parseParameters = function (params) {
    var retval = {};
    try {
        retval = JSON.parse(params);
    }
    catch (e) {
        var lines = params.split(/\n/);
        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];
            var pair = line.split(/\s*=\s*/);
            var key = pair[0];
            var value = pair[1];
            retval[key] = value;
        }
    }
    return retval;
};
var parameters = parseParameters(paramsString);
var promise = efClient.request(restEndpoint, method, parameters, payload);
promise.then(function (res) {
    console.log(res);
    tl.setVariable(resVarName, JSON.stringify(res.response));
    tl.setResult(tl.TaskResult.Succeeded, "Successfully ran API method " + method + " on " + restEndpoint);
}).catch(function (e) {
    console.log(e);
    tl.setResult(tl.TaskResult.Failed, e);
});
