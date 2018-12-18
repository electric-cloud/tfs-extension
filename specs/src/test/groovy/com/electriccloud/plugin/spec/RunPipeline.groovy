package com.electriccloud.plugin.spec

import spock.lang.*

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
        setup: 'update TFS build definition'
        updateTFSBuild(idBuildPipelineTfs)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        def efPipelineId = (allLogs =~ (/Link to the pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/))[0][1]
        println "EF PIPELINE $efPipelineId"
        waitUntil {
            flowRuntimeDetails(efPipelineId)
        }
        def status = flowRuntimeDetails(efPipelineId)

        then: 'the procedure result validation'
        assert efPipelineId
        assert status.completed == "1"
    }

}
