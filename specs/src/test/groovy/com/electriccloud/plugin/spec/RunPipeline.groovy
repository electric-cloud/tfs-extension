package com.electriccloud.plugin.spec

import spock.lang.*

class RunPipeline extends PluginTestHelper {

    @Shared
    def TC = [
            C185309: [ ids: 'C185309, C368057', description: 'Run pipeline'],
            C185314: [ ids: 'C185314', description: 'Run pipeline with parameters (key=value format)'],
            C185317: [ ids: 'C185317', description: 'Run pipeline with parameters (json format) '],
            C185318: [ ids: 'C185318', description: 'Run pipeline with parameters (special symbols)'],
            C368078: [ ids: 'C368078', description: 'wrong project name'],
            C368079: [ ids: 'C368079', description: 'wrong pipeline name'],
            C368082: [ ids: 'C368082', description: 'extra parameter'],
            C368083: [ ids: 'C368083', description: 'missing pipeline parameter'],
            C368084: [ ids: 'C368084', description: 'wrong format of pipeline parameters'],
    ]

    @Shared
    def idBuildPipelineTfs = null,
        tfsBuildDefinitionParams = [
            buildDefinitionName: "QAtest",
            tfsProject: "$tfsProject",
            tfsTaskID: "0442a599-dd0c-4d8d-b991-ace99fa47424",
            inputs: [
                    electricFlowService: "93f0fde8-63fa-4b9b-adf6-a2fb91f5b02a",
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
    def "RunPipeline, Possitive - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.projectName = efProjectName
        tfsBuildDefinitionParams.inputs.pipelineName = efPipelineName
        tfsBuildDefinitionParams.inputs.requiresAdditionalParameters = requiresParams
        tfsBuildDefinitionParams.inputs.additionalParameters = additParams
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

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
        verifyAll {
            efPipelineId
            status.completed == "1"

            allLogs.contains("GET /rest/v1.0/projects/$efProjectName")
            if (testCaseID != TC.C185318) {
                allLogs.contains("GET /rest/v1.0/pipelines/$efPipelineName?projectName=$efProjectName")
                allLogs.contains("POST /rest/v1.0/pipelines?pipelineName=$efPipelineName&projectName=$efProjectName")
            }
            else {
                // ᶃᶄᶆᶇᶈᶉᶊᶋ] == %E1%B6%83%E1%B6%84%E1%B6%86%E1%B6%87%E1%B6%88%E1%B6%89%E1%B6%8A%E1%B6%8B%5D
                allLogs.contains("GET /rest/v1.0/pipelines/qaPipelineSpecialSymbols%E1%B6%83%E1%B6%84%E1%B6%86%E1%B6%87%E1%B6%88%E1%B6%89%E1%B6%8A%E1%B6%8B%5D?projectName=$efProjectName")
                allLogs.contains("POST /rest/v1.0/pipelines?pipelineName=qaPipelineSpecialSymbols%E1%B6%83%E1%B6%84%E1%B6%86%E1%B6%87%E1%B6%88%E1%B6%89%E1%B6%8A%E1%B6%8B%5D&projectName=$efProjectName")
            }
            allLogs.contains("Pipeline run succeeded, runtime name is $efPipelineName")
            if (additionalLogs){
                allLogs =~ additionalLogs
            }
        }
        where: 'The following params will be: '
        testCaseID | efProjectName | efPipelineName                      | requiresParams | additParams                                          | additionalLogs
        TC.C185309 | 'qaProject'   | 'qaPipelineNoVar'                   | 'false'        | ''                                                   | null
        TC.C185314 | 'qaProject'   | 'qaPipelineVar'                     | 'true'         | 'VAR1=C185314'                                       | "\\{ VAR1\\: 'C185314' \\}"
        TC.C185314 | 'qaProject'   | 'qaPipelineVars'                    | 'true'         | 'VAR1=C185314\nVAR2=TEST1'                           | "\\{ VAR1\\: 'C185314', VAR2\\: 'TEST1' \\}"
        TC.C185317 | 'qaProject'   | 'qaPipelineVar'                     | 'true'         | '{\\"VAR1\\":\\"C185317\\"}'                         | "\\{ VAR1\\: 'C185317' \\}"
        TC.C185317 | 'qaProject'   | 'qaPipelineVars'                    | 'true'         | '{\\"VAR1\\":\\"C185317\\", \\"VAR2\\":\\"TEST2\\"}' | "\\{ VAR1\\: 'C185317', VAR2\\: 'TEST2' \\}"
        TC.C185318 | 'qaProject'   | 'qaPipelineSpecialSymbolsᶃᶄᶆᶇᶈᶉᶊᶋ]' | 'true'         |'ᶃᶄᶆᶇᶈᶉᶊᶋ]=C185318'                                   | "\\{ 'ᶃᶄᶆᶇᶈᶉᶊᶋ]'\\: 'C185318' \\}"
    }

    @Unroll
    def "RunPipeline, Negative - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.projectName = efProjectName
        tfsBuildDefinitionParams.inputs.pipelineName = efPipelineName
        tfsBuildDefinitionParams.inputs.requiresAdditionalParameters = requiresParams
        tfsBuildDefinitionParams.inputs.additionalParameters = additParams
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)

        then: 'the procedure result validation'
        verifyAll {
            allLogs =~ additionalLogs
        }
        where: 'The following params will be: '
        testCaseID | efProjectName  | efPipelineName                      | requiresParams | additParams                       | additionalLogs
        TC.C368078 | 'wrongProject' | 'qaPipelineNoVar'                   | 'false'        | ''                                | "##\\[error\\]Project 'wrongProject' does not exist"
        TC.C368079 | 'qaProject'    | 'wrongPipeline'                     | 'false'        | ''                                | "##\\[error\\]Pipeline 'wrongPipeline' does not exist in project 'qaProject'"
        TC.C368082 | 'qaProject'    | 'qaPipelineVar'                     | 'true'         | 'VAR1=C368082\nVAR2=TEST1'        | "##\\[error\\]Extra parameter\\(s\\) to 'qaPipelineVar': VAR2"
        TC.C368083 | 'qaProject'    | 'qaPipelineVar'                     | 'true'         | ''                                | "##\\[error\\]Missing parameter\\(s\\) to 'qaPipelineVar': VAR1"
        TC.C368084 | 'qaProject'    | 'qaPipelineVar'                     | 'true'         | 'VAR1:C368084'                    | "##\\[error\\]Wrong parameters format, either JSON or key=value pairs are required. You have provided\\: VAR1\\:C368084"
    }

}
