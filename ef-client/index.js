"use strict";
exports.__esModule = true;
var url = require("url");
var q = require("q");
var https = require("https");
var querystring = require("querystring");
var fs = require("fs");
var FormData = require("form-data");
var EFClient = (function () {
    function EFClient(endpoint, username, password, skipCertCheck) {
        this.endpoint = url.parse(endpoint + "/rest/v1.0");
        if (!this.endpoint.host) {
            throw new Error("No hostname found");
        }
        this.username = username;
        this.password = password;
        this.skipCertCheck = skipCertCheck;
    }
    EFClient.prototype.getProject = function (projectName) {
        var promise = this.get("/projects/" + querystring.escape(projectName), undefined);
        return promise;
    };
    EFClient.prototype.getPipeline = function (pipelineName, projectName) {
        var promise = this.get("/pipelines/" + querystring.escape(pipelineName), { projectName: projectName });
        return promise;
    };
    EFClient.prototype.runPipeline = function (pipelineName, projectName) {
        return this.post("/pipelines", { pipelineName: pipelineName, projectName: projectName }, "");
    };
    EFClient.prototype.getPort = function () {
        var port = this.endpoint.port ? parseInt(this.endpoint.port) : 443;
        return port;
    };
    EFClient.prototype.request = function (path, method, query, payload) {
        var def = q.defer();
        var endpoint = this.endpoint;
        var port = endpoint.port ? parseInt(endpoint.port) : 443;
        if (this.skipCertCheck) {
            process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        }
        var queryString = "";
        if (query) {
            var pairs = new Array();
            for (var key in query) {
                var value = query[key];
                pairs.push(key + "=" + querystring.escape(value));
            }
            queryString = '?' + pairs.join("&");
        }
        var options = {
            host: endpoint.hostname,
            port: port,
            method: method,
            path: endpoint.path + path + queryString,
            auth: this.username + ':' + this.password,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        };
        var responseString = "";
        var req = https.request(options, function (res) {
            res.setEncoding('utf8');
            res.on('data', function (chunk) {
                responseString += chunk;
            });
            res.on('end', function () {
                var statusCode = res.statusCode;
                if (statusCode == 200) {
                    var responseObject = JSON.parse(responseString);
                    def.resolve(responseObject);
                }
                else {
                    def.reject({ statusCode: statusCode, response: JSON.parse(responseString) });
                }
            });
        }).on('error', function (e) {
            console.log('http request error');
            def.reject(e);
        });
        if (payload) {
            req.write(payload);
        }
        req.end();
        return def.promise;
    };
    EFClient.prototype.post = function (path, query, payload) {
        return this.request(path, 'POST', query, payload);
    };
    EFClient.prototype.put = function (path, query, payload) {
        return this.request(path, 'PUT', query, payload);
    };
    EFClient.prototype.get = function (path, query) {
        return this.request(path, 'GET', query, undefined);
    };
    EFClient.prototype.login = function () {
        return this.post('/sessions', { userName: this.username, password: this.password }, '');
    };
    EFClient.prototype.publishArtifact = function (path, artifactName, artifactVersion, repositoryName, commanderSessionId) {
        var def = q.defer();
        var form = new FormData();
        var stream = fs.createReadStream(path).on('error', function (e) {
            console.log("File stream error", e);
            def.reject(e);
        });
        form.append("files", stream);
        form.append("artifactName", artifactName);
        form.append("artifactVersionVersion", artifactVersion);
        form.append("commanderSessionId", commanderSessionId);
        form.append("action", 'publishArtifact');
        form.append("repositoryName", repositoryName);
        form.append('compress', "1");
        if (this.skipCertCheck) {
            process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        }
        var endpoint = this.endpoint;
        var options = {
            host: endpoint.hostname,
            port: this.getPort(),
            method: 'POST',
            path: '/commander/cgi-bin/publishArtifactAPI.cgi',
            auth: this.username + ':' + this.password,
            protocol: 'https:'
        };
        var req = form.submit(options).on('response', function (res) {
            res.setEncoding('utf8');
            var responseString = "";
            res.on('data', function (chunk) {
                responseString += chunk;
            }).on('end', function () {
                var answer = { statusCode: res.statusCode, response: responseString };
                if (res.statusCode == 200) {
                    def.resolve(answer);
                }
                else {
                    def.reject(answer);
                }
            });
        }).on('error', function (e) {
            console.log("Request error", e);
            def.reject(e);
        });
        return def.promise;
    };
    return EFClient;
}());
exports.EFClient = EFClient;
