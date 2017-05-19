"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const url = require("url");
const q = require("q");
const https = require("https");
const querystring = require("querystring");
const fs = require("fs");
const FormData = require("form-data");
const path = require("path");
const glob_1 = require("glob");
class EFClient {
    constructor(endpoint, username, password, restVersion, skipCertCheck) {
        if (endpoint.match(/\/$/)) {
            endpoint = endpoint.replace(/\/$/, '');
        }
        endpoint += '/rest/' + restVersion;
        this.endpoint = url.parse(endpoint);
        if (!this.endpoint.host) {
            throw new Error("No hostname found");
        }
        this.username = username;
        this.password = password;
        this.skipCertCheck = skipCertCheck;
    }
    getProject(projectName) {
        let promise = this.get("/projects/" + querystring.escape(projectName), undefined);
        return promise;
    }
    getPipeline(pipelineName, projectName) {
        let promise = this.get("/pipelines/" + querystring.escape(pipelineName), { projectName: projectName });
        return promise;
    }
    runPipeline(pipelineName, projectName) {
        return this.post("/pipelines", { pipelineName: pipelineName, projectName: projectName }, "");
    }
    runPipelineWithParameters(pipelineName, projectName, additionalParameters) {
        let list = [];
        for (let parameterName in additionalParameters) {
            list.push({ actualParameterName: parameterName, value: additionalParameters[parameterName] });
        }
        let payload = JSON.stringify({ actualParameter: list });
        return this.post("/pipelines", { pipelineName: pipelineName, projectName: projectName }, payload);
    }
    getRepository(repoName) {
        return this.get('/repositories/' + repoName, {});
    }
    getPort() {
        let port = this.endpoint.port ? parseInt(this.endpoint.port) : 443;
        return port;
    }
    request(path, method, query, payload) {
        var def = q.defer();
        let endpoint = this.endpoint;
        let port = endpoint.port ? parseInt(endpoint.port) : 443;
        if (this.skipCertCheck) {
            process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        }
        let queryString = "";
        if (query) {
            let pairs = new Array();
            for (let key in query) {
                let value = query[key];
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
        let req = https.request(options, (res) => {
            res.setEncoding('utf8');
            res.on('data', (chunk) => {
                responseString += chunk;
            });
            res.on('end', () => {
                let statusCode = res.statusCode;
                if (statusCode < 300) {
                    var responseObject = JSON.parse(responseString);
                    def.resolve(responseObject);
                }
                else {
                    def.reject({ statusCode: statusCode, response: JSON.parse(responseString) });
                }
            });
        }).on('error', (e) => {
            console.log('http request error');
            def.reject(e);
        });
        if (payload) {
            req.write(payload);
        }
        req.end();
        return def.promise;
    }
    post(path, query, payload) {
        return this.request(path, 'POST', query, payload);
    }
    put(path, query, payload) {
        return this.request(path, 'PUT', query, payload);
    }
    get(path, query) {
        return this.request(path, 'GET', query, undefined);
    }
    login() {
        return this.post('/sessions', { userName: this.username, password: this.password }, '');
    }
    findAllFiles(dirPath, acc) {
        fs.readdirSync(dirPath).forEach((filename) => {
            let fullFilename = path.join(dirPath, filename);
            let stat = fs.statSync(fullFilename);
            if (stat.isDirectory()) {
                return this.findAllFiles(fullFilename, acc);
            }
            else {
                acc.push(fullFilename);
            }
        });
        return acc;
    }
    publishArtifact(artifactPath, artifactName, artifactVersion, repositoryName, commanderSessionId) {
        let def = q.defer();
        let form = new FormData();
        let files = glob_1.glob.sync(artifactPath, {});
        files.forEach((filename) => {
            let stat = fs.statSync(filename);
            if (stat.isDirectory()) {
                let files = this.findAllFiles(filename, []);
                files.forEach((filename) => {
                    let stream = fs.createReadStream(filename).on("error", (e) => {
                        console.log("File stream error", e);
                        def.reject(e);
                    });
                    console.log(`Adding file ${filename}`);
                    form.append("files", stream);
                });
            }
            else {
                let stream = fs.createReadStream(filename).on("error", (e) => {
                    console.log("File stream error", e);
                    def.reject(e);
                });
                console.log(`Adding file ${filename}`);
                form.append("files", stream);
            }
        });
        if (files.Length == 0) {
            def.reject(`No files found: ${artifactPath}`);
            return def.promise;
        }
        form.append("artifactName", artifactName);
        form.append("artifactVersionVersion", artifactVersion);
        form.append("commanderSessionId", commanderSessionId);
        form.append("action", 'publishArtifact');
        form.append("repositoryName", repositoryName);
        form.append('compress', "1");
        if (this.skipCertCheck) {
            process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        }
        let endpoint = this.endpoint;
        let options = {
            host: endpoint.hostname,
            port: this.getPort(),
            method: 'POST',
            path: '/commander/cgi-bin/publishArtifactAPI.cgi',
            auth: this.username + ':' + this.password,
            protocol: 'https:'
        };
        let req = form.submit(options).on('response', (res) => {
            res.setEncoding('utf8');
            let responseString = "";
            res.on('data', (chunk) => {
                responseString += chunk;
            }).on('end', () => {
                let answer = { statusCode: res.statusCode, response: responseString };
                if (res.statusCode == 200) {
                    def.resolve(answer);
                }
                else {
                    def.reject(answer);
                }
            });
        }).on('error', (e) => {
            console.log("Request error", e);
            def.reject(e);
        });
        return def.promise;
    }
}
exports.EFClient = EFClient;
//# sourceMappingURL=index.js.map