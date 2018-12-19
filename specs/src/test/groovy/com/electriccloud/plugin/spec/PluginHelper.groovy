package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class PluginTestHelper extends PluginSpockTestSupport {

    @Shared
    def tfsProject              =  'eserbinTFSProject',
        tfsURI                  =  "/tfs/DefaultCollection/$tfsProject/_apis/build/builds",
        tfsURIBuildDefinition   =  "/tfs/DefaultCollection/$tfsProject/_apis/build/definitions",
        apiVersion = '4.0'

    @Shared
    def revisionNumber = 0

    static final String PLUGIN_NAME = 'TFS_extension'
    
    static def         CONFIG_CREATED = false
    static HTTPBuilder http
    static String      authHeaderValue

    static def automationTestsContextRun = System.getenv ('AUTOMATION_TESTS_CONTEXT_RUN') ?: ''

    static String getHost() {
        def host = System.getenv('TFS_HOST')
        assert host
        return host
    }

    static String getToken() {
        def token = System.getenv('TFS_TOKEN')
        assert token
        return token
    }

    def setupSpec() {
        dslFile('dsl/RunPipeline.dsl')
        this.http = new HTTPBuilder(getHost())

        // Doesn't work for me
        this.http.ignoreSSLIssues()
        def authData = ':' + getToken()
        this.authHeaderValue = "Basic ${authData.bytes.encodeBase64().toString()}"
        println "Authorization: $authHeaderValue"

        // Default error handler
        http.handler.failure = { resp ->
            println "Unexpected failure: ${resp.statusLine}"
            println "Code: ${resp.status}"
            println "Body: ${resp.data}"
            println http.dump()
            println "-------------------"
            println resp.dump()
            println "-------------------"
            def result = resp
            println "========================="
        }
    }


    def createConfig(String configName) {
        createPluginConfiguration(
            pluginName,
            configName,
            [
                host: getHost(),
            ],
            getUsername(),
            getPassword(),
            [
                confPath: 'ServiceNow_cfgs',
                recreate: true,
            ]
        )
    }

    def deleteConfig() {
        deleteConfiguration(pluginName, getConfigName())
    }

    def conditionallyDeleteProject(String projectName){
        if (System.getenv("LEAVE_TEST_PROJECTS")){
            return
        }
        dsl "deleteProject projectName: '$projectName'"
    }


    def createTFSBuild(def params, def method=POST, def id=null){
        def repositoryUrl = getHost() + "/tfs/DefaultCollection/"
        def inputs = ''
        for (input in params.inputs){
            inputs += "${input.key}:\"${input.value}\",\n"
        }

        def idLine = ''
        def revisionLine = ''
        if (id){
            idLine = "\"id\": \"$id\","
            revisionLine = "\"revision\": $revisionNumber,"
        }

        def r = http.request( method, JSON ) {
            uri.path = tfsURIBuildDefinition
            if (id) {
                uri.path = tfsURIBuildDefinition + '/' + id
            }
            uri.query = ['api-version' : apiVersion]
            println uri.toString()
            body = """{
    $idLine
    $revisionLine
    "name":"${params.buildDefinitionName}",
    "repository":
    {
        "id": "\$/",
        "type":  "TfsGit",
        "name":  "${params.tfsProject}",
        "url":  "$repositoryUrl",
        "defaultBranch":  "refs/heads/master",
        "clean":  "false",
        "checkoutSubmodules":  false
    },
    "queue":
    {
        "name":"Default"
    },
    process: {
        phases: [
            {
                steps: [
                    {
                        environment: { },
                        enabled: true,
                        continueOnError: false,
                        alwaysRun: false,
                        displayName: "task1",
                        timeoutInMinutes: 0,
                        condition: "succeeded()",
                        refName: "runpipeline1",
                        task: {
                            id: "${params.tfsTaskID}",
                            versionSpec: "1.*",
                            definitionType: "task"
                        },
                        inputs: {
                            $inputs
                        }
                    }
                ],
                name: null,
                jobAuthorizationScope: 0
            }
        ],
        type: 1
    }
}"""
            println body
            headers.'Authorization' = authHeaderValue
            headers.'Content-Type' = 'application/json'
            response.success = { resp, json ->
                if (!id){
                    def tfsBuildId = json._links.self.href.split("/")[-1]
                    return tfsBuildId
                }
            }
        }
    }

    def updateTFSBuild(def id, def params){
        revisionNumber++
        createTFSBuild(params, PUT, id)
    }

    def runTfsPipeline(def pipelineID){
        // Run TFS/AzureDevOps Pipeline
        // https://dev.azure.com/{organization}/{project}/_apis/build/builds?api-version=4.1
        def result = null
        def r = http.request( POST, JSON ) {
            uri.path = tfsURI
            uri.query = ['api-version' : apiVersion]
            println uri.toString()
            body = "{ \"definition\": {\"id\": $pipelineID}}"
            headers.'Authorization' = authHeaderValue
            headers.'Content-Type' = 'application/json'
            response.success = { resp, json ->
                println "LOG URL $json"
                result = json
            }
        }
        def buildID = r._links.self.href.split("/")[-1]
        assert waitUntilTfsBuildCompleted(buildID) == 'completed'
        return r.logs.url
    }

    def deleteBuildDefinitions(def buildDefinitionID){
        def r = http.request(DELETE, JSON) {
            uri.path = tfsURIBuildDefinition + '/' + buildDefinitionID
            uri.query = ['api-version' : apiVersion]
            headers.'Authorization' = authHeaderValue
        }
    }

    def waitUntilTfsBuildCompleted(def buildID){
        def buildStatus = ''
        for (def i=0; i<10; i++) {
            sleep(10000)
            def r = http.request(GET, JSON) {
                uri.path = tfsURI + '/' + buildID
                headers.'Authorization' = authHeaderValue
                response.success = { resp, json ->
                    buildStatus = json.status
                }
            }
            if (buildStatus == 'completed'){
                break;
            }
        }
        return buildStatus
    }

    def getTfsLogLink(def urlLogList){
        def r = http.request( urlLogList, GET, JSON ) {
            println uri.toString()
            headers.'Authorization' = authHeaderValue
            response.success = { resp, json ->
                return json.get('value')
            }
        }
    }

    def getAllTfsLogs(def listOfLinks){
        def allLogs = ""
        for (def urlItem : listOfLinks) {
            def r = http.request( urlItem.url, GET, JSON ) {
                println uri.toString()
                headers.'Authorization' = authHeaderValue
                response.success = { resp, json ->
                    def log = json.get('value')
                    allLogs += log + "\n"
                }
            }
        }
        return allLogs
    }
}

