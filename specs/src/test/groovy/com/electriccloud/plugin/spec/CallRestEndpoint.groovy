package com.electriccloud.plugin.spec

import spock.lang.*

class CallRestEndpoint extends PluginTestHelper {

    @Shared
    def TC = [
            C368195: [ ids: 'C368195', description: 'Method Get'],
            C368173: [ ids: 'C368173', description: 'use spaces in Path'],
            C368197: [ ids: 'C368197', description: 'Method Get with parameters key=value'],
            C368203: [ ids: 'C368203', description: 'Method Get with parameters json'],
            C368198: [ ids: 'C368198', description: 'Method POST with payloads'],
            C368224: [ ids: 'C368224', description: 'Method DELETE'],
            C368208: [ ids: 'C368208', description: 'Method POST with Parameters'],
            C368218: [ ids: 'C368218', description: 'Method POST with Parameters and Payload and special symbols'],
            C368187: [ ids: 'C368187', description: 'use special symbols in Path'],
            C368225: [ ids: 'C368225', description: 'wrong Method'],
            C368226: [ ids: 'C368226', description: 'wrong PATH'],
            C368228: [ ids: 'C368228', description: 'wrong format of parameters'],
            C368229: [ ids: 'C368229', description: 'wrong format of Payload'],


    ]

    @Shared
    logsToVerivy = [
            examplesOfProject:     ["GET /rest/v1.0/projects?", "{ project:", "projectName: 'Default'", "description: 'Default project created during installation.',", "projectName: 'EC-Artifact"],
            spacesInPath:          ["GET /rest/v1.0/projects/Electric%20Cloud?", "projectName: 'Electric Cloud',"],
            getParameters:         ["GET /rest/v1.0/pipelines/qaPipelineNoVar?projectName=qaProject", "{ pipeline:", "pipelineName: 'qaPipelineNoVar'"],
            POST:                  ["POST /rest/v1.0/projects/qaProject/applications?", "Payload: {\"applicationName\":\"qaTest\"}", "{ application: ", "applicationName: 'qaTest',"],
            DELETE:                ["DELETE /rest/v1.0/projects/qaProject/applications/qaTest?"],
            POSTParams:            ["POST /rest/v1.0/projects?projectName=qaProject2", "{ project:", "projectName: 'qaProject2',"],
            POSTParamsAndPayload:  ["POST /rest/v1.0/projects?projectName=qaProject%20%25%3F&description=test%20%3Fdescription", "{ project:", "projectName: 'qaProject %?',", "description: 'test ?description',"],
            pathSpecialSymbols:    ["SHOULD BE updated"],
            wrongMethod:           [(apiVersion != "3.0") ? "[error]Unhandled: Unexpected end of JSON input" : "[error]Unhandled: Unexpected end of input"],
            wrongPath:             ["{ error:", "code: 'NotImplemented'"],
            wrongParams:           ["[error]'projectName' is required and must be between 1 and 255 characters"],
            wrongPayload:          ["[error]'projectName' is required and must be between 1 and 255 characters"],


    ]

    @Shared
    def idBuildPipelineTfs = null,
        tfsServiceEndpointDefault,
        tfsBuildDefinitionParams = [
                buildDefinitionName: "QAtest",
                tfsProject: "$tfsProject",
                tfsTaskID: tfsCallRestEndpointTaskID,
                inputs: [
                        electricFlowService: "",
                        method: "GET",
                        restEndpoint: "/projects",
                        params: "",
                        payload: "",
                        resultVarName: "Build.Projects"
                ]
        ]

    def setupSpec() {
        tfsServiceEndpointDefault = createTfsServiceEndpoint(defaultTfsServiceEndpointParams).id
        tfsBuildDefinitionParams.inputs.electricFlowService = tfsServiceEndpointDefault
        idBuildPipelineTfs = createTFSBuild(tfsBuildDefinitionParams)
    }

    def doCleanupSpec() {
        deleteBuildDefinitions(idBuildPipelineTfs)
        deleteTfsServiceEndpoint(tfsServiceEndpointDefault)
    }

    @Unroll
    def "Call REST, Possitive - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.method = method
        tfsBuildDefinitionParams.inputs.restEndpoint = restEndpoint
        tfsBuildDefinitionParams.inputs.params = params
        tfsBuildDefinitionParams.inputs.payload = payload
        tfsBuildDefinitionParams.inputs.resultVarName = resultVarName
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)

        then: 'the procedure result validation'
        verifyAll {
            for (log in logToVerify){
                allLogs.contains(log)
            }
        }
        where: 'The following params will be: '
        testCaseID | method   | restEndpoint                                | params                                                     | payload                                                                   | resultVarName      | logToVerify
        TC.C368195 | "GET"    | "/projects"                                 | ""                                                         | ""                                                                        | "Build.Projects"   | logsToVerivy.examplesOfProject
        TC.C368173 | "GET"    | "/projects/Electric Cloud"                  | ""                                                         | ""                                                                        | "Build.Projects"   | logsToVerivy.spacesInPath
        TC.C368197 | "GET"    | "/pipelines/qaPipelineNoVar"                | "projectName=qaProject"                                    | ""                                                                        | "Build.Projects"   | logsToVerivy.getParameters
        TC.C368203 | "GET"    | "/pipelines/qaPipelineNoVar"                | '{\\"projectName\\":\\"qaProject\\"}'                      | ""                                                                        | "Build.Projects"   | logsToVerivy.getParameters
        TC.C368198 | "POST"   | "/projects/qaProject/applications"          | ""                                                         | '{\\"applicationName\\":\\"qaTest\\"}'                                    | "Build.Projects"   | logsToVerivy.POST
        TC.C368224 | "DELETE" | "/projects/qaProject/applications/qaTest"   | ""                                                         | ""                                                                        | "Build.Projects"   | logsToVerivy.DELETE
        TC.C368208 | "POST"   | "/projects"                                 | "projectName=qaProject2"                                   | ''                                                                        | "Build.Projects"   | logsToVerivy.POSTParams
        TC.C368218 | "POST"   | "/projects"                                 | "projectName=qaProject %?\ndescription=test ?description"  | '{ \\"resourceName\\":\\"local\\", \\"workspaceName\\":\\"default\\"}'    | "Build.Projects"   | logsToVerivy.POSTParamsAndPayload
//        TC.C368187 | "GET"    | "/projects/qaProject %?"                    | ""                                                         | ""                                                                        | "Build.Projects"   | logsToVerivy.pathSpecialSymbols
    }

    @Unroll
    def "Call REST, Negative - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.method = method
        tfsBuildDefinitionParams.inputs.restEndpoint = restEndpoint
        tfsBuildDefinitionParams.inputs.params = params
        tfsBuildDefinitionParams.inputs.payload = payload
        tfsBuildDefinitionParams.inputs.resultVarName = resultVarName
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)

        then: 'the procedure result validation'
        verifyAll {
            for (log in logToVerify){
                allLogs.contains(log)
            }
        }
        where: 'The following params will be: '
        testCaseID | method   | restEndpoint                         | params                    | payload                            | resultVarName      | logToVerify
        TC.C368225 | "WRONG"  | "/projects"                          | ""                        | ""                                 | "Build.Projects"   | logsToVerivy.wrongMethod
        TC.C368226 | "GET"    | "/wrong"                             | ""                        | ""                                 | "Build.Projects"   | logsToVerivy.wrongPath
        TC.C368228 | "GET"    | "/pipelines/qaPipeline"              | "projectName:qaProject"   | ""                                 | "Build.Projects"   | logsToVerivy.wrongParams
        TC.C368229 | "GET"    | "/projects/qaProject/applications"   | ""                        | "\"applicationName\":\"qaTest\""   | "Build.Projects"   | logsToVerivy.wrongPayload
    }

}