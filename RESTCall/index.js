"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const ef_client_1 = require("ef-client");
const tl = require("vsts-task-lib/task");
var efEndpoint = tl.getInput('electricFlowService', true);
var efBaseUrl = tl.getEndpointUrl(efEndpoint, true);
var trustCerts = tl.getEndpointDataParameter(efEndpoint, 'acceptUntrustedCerts', true);
var restVersion = tl.getEndpointDataParameter(efEndpoint, 'restVersion', true);
var efAuth = tl.getEndpointAuthorization(efEndpoint, true);
let skipCertCheck = trustCerts == 'true';
if (skipCertCheck) {
    console.log("Certificate check is skipped");
}
var efClient = new ef_client_1.EFClient(efBaseUrl, efAuth.parameters['username'], efAuth.parameters['password'], restVersion, skipCertCheck);
var method = tl.getInput('method', true);
var paramsString = tl.getInput('params', false);
var restEndpoint = tl.getInput('restEndpoint', true);
if (!restEndpoint.match(/^\//)) {
    restEndpoint = '/' + restEndpoint;
}
var resVarName = tl.getInput('resultVarName', true);
let payload = tl.getInput('payload', false);
if (!payload) {
    payload = '';
}
let parseParameters = function (params) {
    if (!params) {
        return {};
    }
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
let parameters = parseParameters(paramsString);
console.log("Parameters are:", parameters);
let promise = efClient.request(restEndpoint, method, parameters, payload);
promise.then((res) => {
    console.log(res);
    tl.setVariable(resVarName, JSON.stringify(res.response));
    tl.setResult(tl.TaskResult.Succeeded, `Successfully ran API method ${method} on ${restEndpoint}`);
}).catch((e) => {
    console.log(e);
    let message = 'Cannot run pipeline';
    if (e.response && e.response.error) {
        message = e.response.error.message;
    }
    tl.setResult(tl.TaskResult.Failed, message);
});
//# sourceMappingURL=index.js.map