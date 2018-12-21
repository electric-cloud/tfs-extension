package com.electriccloud.plugin.spec

import spock.lang.*

class TriggerRelease extends PluginTestHelper {

    @Shared
    def TC = [
            C368232: [ ids: 'C368232', description: 'Trigger release'],
            C368233: [ ids: 'C368233', description: 'Trigger release - Starting stage name'],
            C368234: [ ids: 'C368234', description: 'Trigger release -Stages to run'],
            C368235: [ ids: 'C368235', description: 'Requires pipeline parameters - TRUE'],

            C368236: [ ids: 'C368236', description: 'wrong Project Name'],
            C368237: [ ids: 'C368237', description: 'wrong Release'],
            C368238: [ ids: 'C368238', description: 'wrong Starting stage Name'],
            C368241: [ ids: 'C368241', description: 'wrong Stages to run'],
            C368242: [ ids: 'C368242', description: '"stages to run" and "Starting stage Name"'],
            C368243: [ ids: 'C368243', description: 'wrong format of Pipeline parameters'],

    ]

    @Shared
    def idBuildPipelineTfs = null,
        tfsServiceEndpointDefault,
        tfsBuildDefinitionParams = [
                buildDefinitionName: "QAtest",
                tfsProject: "$tfsProject",
                tfsTaskID: tfsTriggerReleaseTaskID,
                inputs: [
                        electricFlowService: "",
                        projectName: "qaProject",
                        releaseName: "qaReleaseNoVar",
                        startingStageName: "",
                        stagesToRun: "",
                        requiresAdditionalParameters: "false",
                        additionalParameters: ""
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
    def "RunPipeline, Possitive - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.projectName = projectName
        tfsBuildDefinitionParams.inputs.releaseName = releaseName
        tfsBuildDefinitionParams.inputs.startingStageName = startingStageName
        tfsBuildDefinitionParams.inputs.stagesToRun = stagesToRun
        tfsBuildDefinitionParams.inputs.requiresAdditionalParameters = requiresAdditionalParameters
        tfsBuildDefinitionParams.inputs.additionalParameters = additionalParameters
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        def efPipelineId = (allLogs =~ (/Link to the release pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/))[0][1]
        println "EF PIPELINE $efPipelineId"
        waitUntil {
            flowRuntimeDetails(efPipelineId).completed
        }
        def status = flowRuntimeDetails(efPipelineId)

        // sometimes "completed" contains 0 instead 1, and it needs some time for receiving 1
        for (def i=0; i < 5; i++) {
            if (status != "1"){
                sleep(5000)
                status = flowRuntimeDetails(efPipelineId)
            }
            else {
                break
            }
        }

        then: 'the procedure result validation'
        verifyAll {
            efPipelineId
            status.completed == "1"
            allLogs.contains("GET /rest/v1.0/projects/$projectName")
            allLogs.contains("GET /rest/v1.0/releases/$releaseName?projectName=$projectName")
            allLogs.contains(logToVerify)
        }
        where: 'The following params will be: '
        testCaseID   | projectName | releaseName       | startingStageName | stagesToRun               | requiresAdditionalParameters | additionalParameters                                | logToVerify
        TC.C368232   | "qaProject" | "qaReleaseNoVar"  |  ""               | ""                        | "false"                      | ""                                                  | "POST /rest/v1.0/releases?projectName=qaProject&releaseName=qaReleaseNoVar"
        TC.C368233   | "qaProject" | "qaReleaseNoVar"  |  "Stage2"         | ""                        | "false"                      | ""                                                  | "POST /rest/v1.0/releases?projectName=qaProject&releaseName=qaReleaseNoVar&startingStage=Stage2"
        TC.C368234   | "qaProject" | "qaReleaseNoVar"  |  ""               | "Stage3"                  | "false"                      | ""                                                  | "Pipeline parameters (converted): {\"stagesToRun\":[\"Stage3\"]}"
        TC.C368234   | "qaProject" | "qaReleaseNoVar"  |  ""               | "Stage2,Stage4"           | "false"                      | ""                                                  | "Pipeline parameters (converted): {\"stagesToRun\":[\"Stage2\",\"Stage4\"]}"
        TC.C368235   | "qaProject" | "qaReleaseVars"   |  ""               | ""                        | "true"                       | "VAR1=TEST1\nVAR2=TEST2"                            | "{\"pipelineParameter\":[{\"pipelineParameterName\":\"VAR1\",\"value\":\"TEST1\"},{\"pipelineParameterName\":\"VAR2\",\"value\":\"TEST2\"}]}"
        TC.C368235   | "qaProject" | "qaReleaseVars"   |  ""               | ""                        | "true"                       | '{\\"VAR1\\":\\"TEST3\\", \\"VAR2\\":\\"TEST4\\"}'  | "{\"pipelineParameter\":[{\"pipelineParameterName\":\"VAR1\",\"value\":\"TEST3\"},{\"pipelineParameterName\":\"VAR2\",\"value\":\"TEST4\"}]}"
    }


    @Unroll
    def "RunPipeline, Negative - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.projectName = projectName
        tfsBuildDefinitionParams.inputs.releaseName = releaseName
        tfsBuildDefinitionParams.inputs.startingStageName = startingStageName
        tfsBuildDefinitionParams.inputs.stagesToRun = stagesToRun
        tfsBuildDefinitionParams.inputs.requiresAdditionalParameters = requiresAdditionalParameters
        tfsBuildDefinitionParams.inputs.additionalParameters = additionalParameters
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        then: 'the procedure result validation'
        verifyAll {
            for (error in errors) {
                allLogs.contains(error)
            }
        }
        where: 'The following params will be: '
        testCaseID | projectName    | releaseName       | startingStageName | stagesToRun               | requiresAdditionalParameters | additionalParameters         | errors
        TC.C368236 | "wrongProject" | "qaReleaseNoVar"  |  ""               | ""                        | "false"                      | ""                           | ["[error]Project 'wrongProject' does not exist"]
        TC.C368237 | "qaProject"    | "wrongRelease"    |  ""               | ""                        | "false"                      | ""                           | ["[error]Release 'wrongRelease' does not exist in project 'qaProject'"]
        TC.C368238 | "qaProject"    | "qaReleaseNoVar"  |  "wrongStage"     | ""                        | "false"                      | ""                           | ["[error]No such starting stage 'wrongStage'"]
        TC.C368241 | "qaProject"    | "qaReleaseNoVar"  |  ""               | "wrongStage"              | "false"                      | ""                           | ["[error]No such stage 'wrongStage' exists in pipeline 'pipeline_qaReleaseNoVar' in project 'qaProject'"]
        TC.C368242 | "qaProject"    | "qaReleaseNoVar"  |  ""               | ""                        | "true"                       | "<VAR1>:TEST1\n<VAR2>:TEST2" | ["##[error]Cannot trigger release", "Wrong parameters format, either JSON or key=value pairs are required."]
        TC.C368243 | "qaProject"    | "qaReleaseNoVar"  |  "Stage2"         | "Stage2,Stage4"           | "false"                      | ""                           | ["[error]Invalid argument specified. Please provide one of 'startingStage' or 'stagesToRun' or 'ec_stagesToRun'"]

    }
}
