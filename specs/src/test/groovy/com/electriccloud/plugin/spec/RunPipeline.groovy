package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class RunPipeline extends PluginTestHelper {

    @Shared
    def tfsURI                  =  '/tfs/DefaultCollection/eserbinTFSProject/_apis/build/builds',
        tfsURIBuildDefinition   =  '/tfs/DefaultCollection/eserbinTFSProject/_apis/build/definitions',
        idBuildPipelineTfs = null,
        apiVersion = '4.0'

    def setup() {
        dslFile('dsl/RunPipeline.dsl')
        idBuildPipelineTfs = createTFSBuild()
    }

    def doCleanupSpec() {
        deleteBuildDefinitions(idBuildPipelineTfs)
    }

    def "RunPipeline, only required fields filled - Sanity"(){
        when: 'the procedure runs'
            updateTFSBuild(idBuildPipelineTfs)
            def urlLogList = runTfsPipeline(idBuildPipelineTfs)
            def r = getTfsLogLink(urlLogList)
            def ef_pipeline_id = null

            for (def urlItem : r) {

                ef_pipeline_id = http.request( urlItem.url, GET, JSON ) {
                    println uri.toString()
                    headers.'Authorization' = authHeaderValue

                    response.success = { resp, json ->
                        //println json
                        String log = json.get('value')
                        if( !( log =~ /ElectricFlow - Run Pipeline/ ) ) {
                            return null
                        }

                        def finder = log =~ /Link to the pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/
                        if( !finder) {
                            return null
                        }
                        if( !finder.find(1) ) {
                            return null
                        }
                        return finder.group(1)
                    }
                }

                if( ef_pipeline_id != null ) {
                    break;
                }
            }
            println "EF PIPELINE $ef_pipeline_id"
            assert ef_pipeline_id
        then: 'the procedure result validation'
            waitUntil {
                flowRuntimeDetails(ef_pipeline_id)
            }
            def status = flowRuntimeDetails(ef_pipeline_id)
            assert status.completed == "1"
//
//         where:
//             id << [1]
    }

    def createTFSBuild(){
        def buildDefinitionName = "QAtest"
        def tfsProject = "eserbinTFSProject"
        def repositoryUrl = getHost() + "/tfs/DefaultCollection/"

        // 0442a599-dd0c-4d8d-b991-ace99fa47424 - run pipeline
        def tfsTaskID = "0442a599-dd0c-4d8d-b991-ace99fa47424"

        def tfsConfigID = "93f0fde8-63fa-4b9b-adf6-a2fb91f5b02a"
        def efProjectName = "qaProject"
        def efPipelineName = "qaPipeline"
        def requiresAdditionalParameters = "false"
        def additionalParameters = ""

        def result = null
        def r = http.request( POST, JSON ) {
        uri.path = tfsURIBuildDefinition
        uri.query = ['api-version' : apiVersion]
        println uri.toString()
        body = """{
    "name":"$buildDefinitionName",
    "repository":
    {
        "id": "\$/", 
        "type":  "TfsGit",
        "name":  "$tfsProject", 
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
                            id: "$tfsTaskID",
                            versionSpec: "1.*",
                            definitionType: "task"
                        },
                        inputs: {
                            electricFlowService: "$tfsConfigID",
                            projectName: "$efProjectName",
                            pipelineName: "$efPipelineName",
                            requiresAdditionalParameters: "$requiresAdditionalParameters",
                            additionalParameters: "$additionalParameters"
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
        headers.'Authorization' = authHeaderValue
        headers.'Content-Type' = 'application/json'
        response.success = { resp, json ->
             def tfsBuildId = json._links.self.href.split("/")[-1]
             return tfsBuildId
          }
        }
    }

    def updateTFSBuild(def id, def tfsConfigID = "93f0fde8-63fa-4b9b-adf6-a2fb91f5b02a",
                       def efProjectName = "qaProject",
                       def efPipelineName = "qaPipeline",
                       def requiresAdditionalParameters = "true",
                       def additionalParameters = "VAR1=test"){
        def buildDefinitionName = "QAtest"
        def tfsProject = "eserbinTFSProject"
        def repositoryUrl = getHost() + "/tfs/DefaultCollection/"
        // 0442a599-dd0c-4d8d-b991-ace99fa47424 - run pipeline
        def tfsTaskID = "0442a599-dd0c-4d8d-b991-ace99fa47424"

        def result = null
        def r = http.request( PUT, JSON ) {
            uri.path = tfsURIBuildDefinition + '/' + id
            uri.query = ['api-version' : apiVersion]
            println uri.toString()
            body = """{
    "id": "$id",
    "name":"$buildDefinitionName",
    "revision": 1,
    "repository":
    {
        "id": "\$/",
        "type":  "TfsGit",
        "name":  "$tfsProject",
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
                            id: "$tfsTaskID",
                            versionSpec: "1.*",
                            definitionType: "task"
                        },
                        inputs: {
                            electricFlowService: "$tfsConfigID",
                            projectName: "$efProjectName",
                            pipelineName: "$efPipelineName",
                            requiresAdditionalParameters: "$requiresAdditionalParameters",
                            additionalParameters: "$additionalParameters"
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
            headers.'Authorization' = authHeaderValue
            headers.'Content-Type' = 'application/json'
            response.success = { resp, json ->
                println json
            }
        }
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

}
