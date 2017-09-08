import url = require('url');
import q = require('q');
import http = require('http');
import https = require('https');
import querystring = require('querystring');
import fs = require('fs');
import FormData = require('form-data');
import path = require('path');
import {glob} from "glob";

class EFClient {
    endpoint: url.Url;
    username: string;
    password: string;
    skipCertCheck: boolean;
    restVersion: string;

    constructor(endpoint: string, username: string, password: string, restVersion: string, skipCertCheck: boolean) {
        if (endpoint.match(/\/$/)) {
            endpoint = endpoint.replace(/\/$/, '');
        }
        endpoint += '/rest/' + restVersion;
        this.endpoint = url.parse(endpoint);

        if(!this.endpoint.host) {
            throw new Error("No hostname found");
        }
        this.username = username;
        this.password = password;
        this.skipCertCheck = skipCertCheck;
    }

    getProject(projectName: string) {
       let promise = this.get("/projects/" + querystring.escape(projectName), undefined);
       return promise;
    }

    getPipeline(pipelineName: string, projectName: string) {
        let promise = this.get("/pipelines/" + querystring.escape(pipelineName), {projectName: projectName});
        return promise;
    }

    runPipeline(pipelineName: string, projectName: string) {
        return this.post("/pipelines", {pipelineName: pipelineName, projectName: projectName}, "");
    }

    runPipelineWithParameters(pipelineName: string, projectName: string, additionalParameters: any) {
        let list = [];
        for(let parameterName in additionalParameters) {
            list.push({actualParameterName: parameterName, value: additionalParameters[parameterName]});
        }
        let payload = JSON.stringify({actualParameter: list});
        return this.post("/pipelines", {pipelineName: pipelineName, projectName: projectName}, payload)
    }

    getRepository(repoName: string) {
        return this.get('/repositories/' + repoName, {});
    }

    getPort() {
        let port = this.endpoint.port ? parseInt(this.endpoint.port) : 443;
        return port;
    }

    request(path: string, method: string, query: any, payload: string) {
        var def = q.defer();
        let endpoint = this.endpoint;

        let port = endpoint.port ? parseInt(endpoint.port) : 443;
        if (this.skipCertCheck) {
            process.env.NODE_TLS_REJECT_UNAUTHORIZED = "0";
        }

        let queryString = "";
        if (query) {
            let pairs = new Array();
            for(let key in query) {
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
        console.log(`${method} ${options.path}`);

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
                    def.reject({statusCode: statusCode, response: JSON.parse(responseString)});
                }
            })
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

    post(path: string, query: any, payload: string) {
        return this.request(path, 'POST', query, payload);
    }

    put(path: string, query: any, payload: string) {
        return this.request(path, 'PUT', query, payload);
    }

    get(path: string, query: any) {
        return this.request(path, 'GET', query, undefined);
    }

    login() {
        return this.post('/sessions', {}, JSON.stringify({userName: this.username, password: this.password}));
    }

    findAllFiles(dirPath: string, acc: Array<string>) {
        fs.readdirSync(dirPath).forEach((filename: string) => {
            let fullFilename = path.join(dirPath, filename)
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

    publishArtifact(artifactPath: string, artifactName: string, artifactVersion: string, repositoryName: string, commanderSessionId: string) {
        let def = q.defer();

        let form = new FormData();
        let files = glob.sync(artifactPath, {});
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
                let answer = {statusCode: res.statusCode, response: responseString};
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
export { EFClient };
