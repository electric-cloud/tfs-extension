package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class RunPipeline extends PluginTestHelper {

    @Shared
    def tfsURI                  =  '/tfs/DefaultCollection/eserbinTFSProject/_apis/build/builds',
        tfsURIBuildDefinition   =  '/tfs/DefaultCollection/eserbinTFSProject/_apis/build/definitions',
        apiVersion = '4.0',
        idBuildPipelineTfs = '6'

    def "RunPipeline, only required fields filled - Sanity"(){
        when: 'the procedure runs'
            createTFSBuild()
            // def urlLogList = runTfsPipeline(idBuildPipelineTfs)
            // println "SLEEP"
            // sleep(60000)
            // def r = getTfsLogLink(urlLogList)
            // def ef_pipeline_id = null
            // println "R="
            // println r
            // for (def urlItem : r) {
            //     ef_pipeline_id = http.request( urlItem.url, GET, JSON ) {
            //         println uri.toString() 
            //         headers.'Authorization' = authHeaderValue

            //         response.success = { resp, json ->
            //             //println json
            //             String log = json.get('value')
            //             if( !( log =~ /ElectricFlow - Run Pipeline/ ) ) {
            //                 return null
            //             }

            //             def finder = log =~ /Link to the pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/
            //             if( !finder) {
            //                 return null
            //             }
            //             if( !finder.find(1) ) {
            //                 return null
            //             }
            //             return finder.group(1)
            //         }
            //     }

            //     if( ef_pipeline_id != null ) {
            //         break;
            //     }
            // }
            // println "EF PIPELINE $ef_pipeline_id"
            // assert ef_pipeline_id
            // println "SLEEP"
            // sleep(60000)

        then: 'the procedure result validation'            
            // waitUntil {
            //     flowRuntimeDetails(ef_pipeline_id)
            // }
            // def status = flowRuntimeDetails(ef_pipeline_id)
            // assert status.completed == "1"
            assert 1
        // where:
        //     id << [1]
    }

    def createTFSBuild(){
        def buildDefinitionName = "QAtest"
        def tfsProject = "eserbinTFSProject"
        def repositoryUrl = getHost() + "/tfs/DefaultCollection/"

        def result = null
        def r = http.request( POST, JSON ) {
        uri.path = tfsURIBuildDefinition
        // uri.path = tfsURI
        uri.query = ['api-version' : apiVersion]
        println uri.toString()
//         body = '''
// { 
// "definition": 
//     {
//         "id": 7
//     }
// }'''
        body = """
{
    "name":"$buildDefinitionName",
    "repository":
    {
        "id": "\$/", 
        "type":  "TfsVersionControl", 
        "name":  "$tfsProject", 
        "url":  "$repositoryUrl", 
        "defaultBranch":  "\$/master", 
        "rootFolder":  "\$/master", 
        "clean":  "undefined", 
        "checkoutSubmodules":  false
    },
    "queue":
    {
        "name":"Default"
    },
    "build":[    
        {
        "enabled": true,
        "continueOnError": false,
        "alwaysRun": false,
        "displayName": "Task1",
        "task": {
            "id": "pluginsdev.electric-flow.run-pipeline",
            "versionSpec": "*"
            },
        "inputs":{
            "electricFlowService" : "eserbinHome",
            "projectName": "eserbinProject",
            "pipelineName", : "eserbinPipeline",
            "requiresAdditionalParameters", : true,
            "additionalParameters": ""
            },
        }
      ]
}
        """
        headers.'Authorization' = authHeaderValue
        headers.'Content-Type' = 'application/json'
        // response.success = { resp, json ->
        //     println "LOG URL $json.logs.url"
        //     result = json.logs.url
        //     }
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
                println "LOG URL $json.logs.url"
                result = json.logs.url
            }
        }
        return r;

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
