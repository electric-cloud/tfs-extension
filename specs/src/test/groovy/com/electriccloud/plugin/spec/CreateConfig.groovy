package com.electriccloud.plugin.spec

import spock.lang.*


class CreateConfig extends PluginTestHelper {


    @Shared
    def TC = [
            C185308: [ids: 'C185308, C368025', description: 'create configuration'],
            C185532: [ids: 'C185532', description: 'create configuration with non-ascii symbols ᶃᶄᶆᶇᶈᶉᶊᶋ'],
            C368045: [ids: 'C368045', description: 'Update Connection name'],
            C368046: [ids: 'C368046', description: 'Update Server URL'],
            C185381: [ids: 'C185381', description: 'update user`s password'],
            C368052: [ids: 'C368052', description: 'Wrong credentials'],
            C368054: [ids: 'C368054', description: 'Wrong url'],
            C368027: [ids: 'C368027', description: 'Accept untrusted SSL certificates - FALSE, use server with untursted SSL certificates (Run with each procedure)'],


    ]

    @Shared
    def errorLog = (apiVersion == "4.0") ? 'Unexpected token < in JSON at position 0' : 'Unhandled: Unexpected token <'

    @Shared
    def idBuildPipelineTfs = null,
        tfsBuildDefinitionParams = [
                buildDefinitionName: "QAtest",
                tfsProject: "$tfsProject",
                tfsTaskID: tfsRunPipelineTaskID,
                inputs: [
                        electricFlowService: "",
                        projectName: "qaProject",
                        pipelineName: "qaPipelineNoVar",
                        requiresAdditionalParameters: "false",
                        additionalParameters: ""
                ]
        ]

    def setupSpec() {
        idBuildPipelineTfs = createTFSBuild(tfsBuildDefinitionParams)
    }

    def doCleanupSpec() {
        deleteBuildDefinitions(idBuildPipelineTfs)
    }

    @Unroll
    def "CreateConfig - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'set parameters'
        def params = [
                restVersion: restVersion,
                acceptUntrustedCerts: acceptUntrustedCerts,
                name: name,
                url: url,
                username: username,
                password: password
        ]
        when: 'the procedure runs'
        def result = createTfsServiceEndpoint(params)
        def efPipelineId
        def status
        tfsBuildDefinitionParams.inputs.electricFlowService = result.id
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        if (!errorLogs) {
            efPipelineId = (allLogs =~ (/Link to the pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/))[0][1]
            println "EF PIPELINE $efPipelineId"
            waitUntil {
                flowRuntimeDetails(efPipelineId)
            }
            status = flowRuntimeDetails(efPipelineId)
        }
        then: 'the procedure result validation'
        verifyAll {
            result.id
            result.data.restVersion == restVersion
            result.data.acceptUntrustedCerts == acceptUntrustedCerts
            result.name == name
            result.url == url
            if (!errorLogs){
                efPipelineId
                status.completed == "1"
            }
            else{
                allLogs.contains(errorLogs)
            }
        }
        cleanup:
        deleteTfsServiceEndpoint(result.id)

        where: 'The following params will be: '
        testCaseID | restVersion   | acceptUntrustedCerts  | name           | url                | username    | password   |  errorLogs
        TC.C185308 | "v1.0"        | "true"                | "testConfig1"  | efURL              | 'admin'     | 'changeme' | false
        TC.C185532 | "v1.0"        | "true"                | "ᶃᶄᶆᶇᶈᶉᶊᶋ"     | efURL              | 'ᶃᶄᶆᶇᶈᶉᶊᶋ'  | 'ᶃᶄᶆᶇᶈᶉᶊᶋ'  | '[error]No credentials/session found in this request'
        TC.C368052 | "v1.0"        | "true"                | "testConfig2"  | efURL              | 'admin1'    | 'changeme1'| '[error]No credentials/session found in this request'
        TC.C368054 | "v1.0"        | "true"                | "testConfig3"  | 'http://test.com/' | 'admin'     | 'changeme' | errorLog
        TC.C368027 | "v1.0"        | "false"               | "testConfig1"  | efURL              | 'admin'     | 'changeme' | 'Error: self signed certificate'
    }

    @Unroll
    def "Update,  - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'set parameters'
        def params = defaultTfsServiceEndpointParams
        when: 'the procedure runs'
        def result = createTfsServiceEndpoint(params)
        params = [
                restVersion: restVersion,
                acceptUntrustedCerts: acceptUntrustedCerts,
                name: name,
                url: url,
                username: username,
                password: password
        ]
        result = updateTfsServiceEndpoint(result.id, params)
        def efPipelineId
        def status
        tfsBuildDefinitionParams.inputs.electricFlowService = result.id
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        if (!errorLogs) {
            efPipelineId = (allLogs =~ (/Link to the pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/))[0][1]
            println "EF PIPELINE $efPipelineId"
            waitUntil {
                flowRuntimeDetails(efPipelineId)
            }
            status = flowRuntimeDetails(efPipelineId)
        }
        then: 'the procedure result validation'
        verifyAll {
            result.id
            result.data.restVersion == restVersion
            result.data.acceptUntrustedCerts == acceptUntrustedCerts
            result.name == name
            result.url == url
            if (!errorLogs){
                efPipelineId
                status.completed == "1"
            }
            else{
                allLogs.contains(errorLogs)
            }
        }
        cleanup:
        deleteTfsServiceEndpoint(result.id)

        where: 'The following params will be: '
        testCaseID | restVersion   | acceptUntrustedCerts  | name              | url                | username    | password    | errorLogs
        TC.C368045 | "v1.0"        | "true"                | "updatedConfig1"  | efURL              | 'admin'     | 'changeme'  | false
        TC.C368046 | "v1.0"        | "true"                | "defaultEF"       | 'http://test.com/' | 'admin'     | 'changeme'  | errorLog
        TC.C368381 | "v1.0"        | "true"                | "updatedConfig1"  | efURL              | 'admin1'    | 'changeme1' | '[error]No credentials/session found in this request'
    }

}