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

    createFlowRuntimeLink(endpoint: string, pipelineId: string, flowRuntimeId: string) {
        endpoint = endpoint.replace(/\/$/, '');
        let url = endpoint + '/flow/#pipeline-run/' + pipelineId + '/' + flowRuntimeId;
        return url;
    }

    getProject(projectName: string) {
       let promise = this.get("/projects/" + querystring.escape(projectName), undefined);
       return promise;
    }

    getPipeline(pipelineName: string, projectName: string) {
        let promise = this.get("/pipelines/" + querystring.escape(pipelineName), {projectName: projectName});
        return promise;
    }

    getRelease(releaseName: string, projectName: string) {
        console.log("GET RELEASE")
        let promise = this.get("/releases/" + querystring.escape(releaseName), {projectName: projectName});
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
        console.log("Pipeline parameters (raw):", additionalParameters);
        console.log("Pipeline parameters (converted):", payload);
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

    parseParameters(params: string) {
        let retval = {};
        try {
            retval = JSON.parse(params);
        } catch(e) {
            if (params.match(/=/)) {
                let lines = params.split(/\n/);
                for (let i = 0; i < lines.length; i++) {
                    let line = lines[i];
                    let pair = line.split(/\s*=\s*/);
                    let key = pair[0];
                    let value = pair[1];
                    retval[key] = value;
                }
            }
            else {
                var message = `Wrong parameters format, either JSON or key=value pairs are required. You have provided: ${params}`;
                throw(message);
            }
        }
        return retval;
    }

    publishArtifact(artifactPath: string, artifactName: string, artifactVersion: string, repositoryName: string, commanderSessionId: string) {
        let def = q.defer();

        let form = new FormData();
        let root = process.cwd();
        console.log(`Root path (on agent): ${root}`);
        console.log(`Artifact path ${artifactPath}`);

        let files = glob.sync(artifactPath, {});
        files.forEach((filename) => {
            let stat = fs.statSync(filename);

            // Currently results returned from glob on windows will return '/' as the separator
            // We need convert separators to native
            if(process.platform === "win32") {
                filename = filename.replace(/\//g, path.sep);
            }

            let relative = path.relative(artifactPath, filename);
            if(relative === "") {
                relative = path.basename(filename);
            }

            if (stat.isDirectory()) {
                let files = this.findAllFiles(filename, []);
                files.forEach((filename) => {
                    let relative = path.relative(artifactPath, filename);
                    if(relative === "") {
                        relative = path.basename(filename);
                    }

                    let stream = fs.createReadStream(filename).on("error", (e) => {
                        console.log("File stream error", e);
                        def.reject(e);
                    });
                    console.log(`Adding file ${filename} with relative path ${relative}`);
                    form.append("files", stream, {
                        filepath: relative
                    });

                });
            }
            else {
                let stream = fs.createReadStream(filename).on("error", (e) => {
                    console.log("File stream error", e);
                    def.reject(e);
                });
                console.log(`Adding file ${filename} with relative path ${relative}`);
                form.append("files", stream, {
                    filepath: relative
                });
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

    release(projectName: string, releaseName: string, startingStageName: string, stagesToRun: string, additionalParameters: any) {
        let query = { projectName: projectName, releaseName: releaseName };
        if(startingStageName) {
            query["startingStage"] = startingStageName;
        }

        let body = {};

        if(additionalParameters) {
            let list = [];
            for(let parameterName in additionalParameters) {
                list.push({pipelineParameterName: parameterName, value: additionalParameters[parameterName]});
            }
            body["pipelineParameter"] = list;
        }

        if(stagesToRun) {
            body["stagesToRun"] = stagesToRun.split(",");
        }

        let payload = JSON.stringify(body);
        console.log("Pipeline parameters (raw):", additionalParameters);
        console.log("Pipeline parameters (converted):", payload);
        return this.post("/releases", query, payload)
    }
}
export { EFClient };
