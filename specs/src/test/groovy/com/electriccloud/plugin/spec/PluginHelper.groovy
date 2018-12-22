package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class PluginTestHelper extends PluginSpockTestSupport {

    @Shared
    def efURL                     =  System.getenv('EF_URL'),
        tfsProject                =  System.getenv('TFS_PROJECT'),
        isAzure                   =  (System.getenv('IS_AZURE')),
        tfsURI                    =  isAzure ? "/pluginsdev/$tfsProject/_apis/build/builds/" : "/tfs/DefaultCollection/$tfsProject/_apis/build/builds",
        tfsURIBuildDefinition     =  isAzure ? "/pluginsdev/$tfsProject/_apis/build/definitions" : "/tfs/DefaultCollection/$tfsProject/_apis/build/definitions",
        tfsURIServiceEndpoint     =  isAzure ? "/pluginsdev/$tfsProject/_apis/serviceendpoint/endpoints" : "/tfs/DefaultCollection/$tfsProject/_apis/distributedtask/serviceendpoints",
        tfsRunPipelineTaskID      =  "0442a599-dd0c-4d8d-b991-ace99fa47424",
        tfsCallRestEndpointTaskID =  "cd267176-2716-4cf7-b57b-420b126ec3da",
        tfsPublishArtifactTaskID  =  "0e2424a3-42b6-48f5-b3fa-ac6ed16d4c57",
        tfsTriggerReleaseTaskID   =  "41e66e30-f95f-11e8-a9f6-d16792ff02ec",
        apiVersion = System.getenv('API_VERSION'),
        apiVersion2 = System.getenv('API_VERSION2'),
        username = System.getenv('USER'),
        password = System.getenv('PASSWORD')

    @Shared
    def currentTfsBuildRunID

    @Shared
    def defaultTfsServiceEndpointParams = [
            restVersion: "v1.0",
            acceptUntrustedCerts: "true",
            name: "defaultServiceEndpointEF",
            url: efURL,
            username: username,
            password: password
    ]

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
        if (isAzure){
            repositoryUrl = getHost() + "/pluginsdev/"
        }
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
            if (apiVersion == "3.0"){
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
    build: [
                    {
                        enabled: true,
                        continueOnError: false,
                        alwaysRun: false,
                        displayName: "task1",
                        timeoutInMinutes: 0,
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
}"""

            }

            if (isAzure){
                body = """{
    $idLine
    $revisionLine
    "name":"${params.buildDefinitionName}",
    "repository":
    {
        "id": "\$/",
        "type":  "TfsVersionControl",
        "name":  "${params.tfsProject}",
        "url":  "$repositoryUrl",
        "defaultBranch":  "\$/TestProject",
        "rootFolder": "\$/TestProject",
        "clean":  "false",
        "checkoutSubmodules":  false
    },
    queue: {
        name: "Hosted VS2017",
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
                name: "Agent job",
                refName: "Phase_1",
                condition: "succeeded()",
                target: {
                    executionOptions: {
                        type: 0
                    },
                    allowScriptsAuthAccessOption: false,
                    type: 1
                },
                jobAuthorizationScope: "projectCollection",
                jobCancelTimeoutInMinutes: 1
            }
        ],
        type: 1
    }
}"""

            }

            println body
            headers.'Authorization' = authHeaderValue
            headers.'Content-Type' = 'application/json'
            response.success = { resp, json ->
                if (!id){
                    def tfsBuildId = json._links.self.href.split("/")[-1]
                    if (isAzure){
                        tfsBuildId = tfsBuildId.split("\\?")[0]
                    }
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
        currentTfsBuildRunID = buildID
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
        for (def i=0; i<30; i++) {
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

    def createTfsServiceEndpoint(def params){
        def result = null
        def r = http.request( POST, JSON ) {
            uri.path = tfsURIServiceEndpoint
            uri.query = ['api-version' : apiVersion2]
            println uri.toString()
            body = """
{
  "data": {
  "restVersion": "${params.restVersion}",
  "acceptUntrustedCerts": "${params.acceptUntrustedCerts}"
  },
  "name": "${params.name}",
  "type": "electricFlow",
  "url": "${params.url}",
  "authorization": {
    "parameters": {
      "username": "${params.username}",
      "password": "${params.password}"
    },
    "scheme": "UsernamePassword"
  },
  "isReady": true
}
"""
            headers.'Authorization' = authHeaderValue
            headers.'Content-Type' = 'application/json'
            response.success = { resp, json ->
                return json
            }
        }
    }

    def updateTfsServiceEndpoint(def id, def params){
        def result = null
        def r = http.request( PUT, JSON ) {
            uri.path = tfsURIServiceEndpoint + '/' + id
            uri.query = ['api-version' : apiVersion2]
            println uri.toString()
            body = """
{
  "id": "$id",
  "data": {
  "restVersion": "${params.restVersion}",
  "acceptUntrustedCerts": "${params.acceptUntrustedCerts}"
  },
  "name": "${params.name}",
  "type": "electricFlow",
  "url": "${params.url}",
  "authorization": {
    "parameters": {
      "username": "${params.username}",
      "password": "${params.password}"
    },
    "scheme": "UsernamePassword"
  },
  "isReady": true
}
"""
            headers.'Authorization' = authHeaderValue
            headers.'Content-Type' = 'application/json'
            response.success = { resp, json ->
                return json
            }
        }
    }

    def deleteTfsServiceEndpoint(def id){
        def r = http.request(DELETE, JSON) {
            uri.path = tfsURIServiceEndpoint + '/' + id
            uri.query = ['api-version' : apiVersion2]
            headers.'Authorization' = authHeaderValue
        }
    }

}

