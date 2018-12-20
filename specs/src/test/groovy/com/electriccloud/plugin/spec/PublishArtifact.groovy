package com.electriccloud.plugin.spec

import spock.lang.*
import groovy.util.XmlSlurper

class PublishArtifact extends PluginTestHelper {

    @Shared
    def TC = [
            C185319: [ ids: 'C185319, C368147', description: 'publish a nested folders/files as artifact'],
            C234285: [ ids: 'C234285', description: 'publish artifact using using patter'],
            C185333: [ ids: 'C185333', description: 'publish 1 file as artifact'],
            C368146: [ ids: 'C368146', description: 'specify parameter Repository Name'],

            C368148: [ ids: 'C368148', description: 'wrong Artifact Path'],
            C368150: [ ids: 'C368150', description: 'wrong format of Artifact Name'],
            C368170: [ ids: 'C368170', description: 'Artifact version contains forbidden symbols'],
            C368171: [ ids: 'C368171', description: 'wrong Repository Name'],

    ]

    @Shared
    def xml1 = """
<manifest version="2" .*>
  <file name="README.md" .*/>
  <directory name="folder1">
    <directory name="folder2">
      <file name="test3" .*/>
    </directory>
    <directory name="folder3">
      <file name="test4" .*/>
    </directory>
    <file name="test1" .*/>
    <file name="test2" .*/>
  </directory>
""",
    xml2 = """
<manifest version="2" .*>
  <directory name="folder2">
    <file name="test3" .*/>
  </directory>
  <directory name="folder3">
    <file name="test4" .*/>
  </directory>
  <file name="test1" .*/>
  <file name="test2" .*/>
</manifest>
""",
    xml3 = """
<manifest version="2" .*>
  <file name="test1" .*/>
</manifest>
""",
    xml4 = """
<manifest version="2" .*>
  <file name="test1" .*/>
  <file name="test3" .*/>
  <file name="test2" .*/>
  <file name="test4" .*/>
</manifest>
"""

    @Shared
    def idBuildPipelineTfs = null,
        tfsServiceEndpointDefault,
        tfsBuildDefinitionParams = [
                buildDefinitionName: "QAtest",
                tfsProject: "$tfsProject",
                tfsTaskID: tfsPublishArtifactTaskID,
                inputs: [
                        electricFlowService: "",
                        artifactPath: "*",
                        artifactName: "com:QAtest",
                        artifactVersion: "\$(Build.BuildNumber)",
                        repositoryName: "qaRepository"
                ]
        ]

    def setupSpec() {
        dsl """ repository {
repositoryName = "testQA"
url = "https://localhost:8200"
}
"""
        tfsServiceEndpointDefault = createTfsServiceEndpoint(defaultTfsServiceEndpointParams).id
        tfsBuildDefinitionParams.inputs.electricFlowService = tfsServiceEndpointDefault
        idBuildPipelineTfs = createTFSBuild(tfsBuildDefinitionParams)
    }

    def doCleanupSpec() {
        deleteBuildDefinitions(idBuildPipelineTfs)
        deleteTfsServiceEndpoint(tfsServiceEndpointDefault)
    }

    @Unroll
    def "Publish Artifact, Possitive - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.artifactPath = artifactPath
        tfsBuildDefinitionParams.inputs.artifactName = artifactName
        tfsBuildDefinitionParams.inputs.artifactVersion = artifactVersion
        tfsBuildDefinitionParams.inputs.repositoryName = repositoryName
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        def text = dsl "['cat', '/opt/EC/repository-data/com/QArepo/$currentTfsBuildRunID/manifest'].execute().text"
        then: 'the procedure result validation'
        verifyAll {
            text.value =~ xml
        }
        where: 'The following params will be: '
        testCaseID | artifactPath     | artifactName | artifactVersion              | repositoryName | xml
        TC.C185319 | "*"              | "com:QArepo" | "\$(Build.BuildNumber)"      | "default"      | xml1
        TC.C234285 | "folder1/*"      | "com:QArepo" | "\$(Build.BuildNumber)"      | "default"      | xml2
        TC.C234285 | "folder1"        | "com:QArepo" | "\$(Build.BuildNumber)"      | "default"      | xml4
        TC.C185333 | "folder1/test1"  | "com:QArepo" | "\$(Build.BuildNumber)"      | "default"      | xml3
        TC.C368146 | "*"              | "com:QArepo" | "\$(Build.BuildNumber)"      | "testQA"       | xml1
    }

    @Unroll
    def "Publish Artifact, Negative - Sanity: #testCaseID.ids #testCaseID.description"(){
        setup: 'update TFS build definition'
        tfsBuildDefinitionParams.inputs.artifactPath = artifactPath
        tfsBuildDefinitionParams.inputs.artifactName = artifactName
        tfsBuildDefinitionParams.inputs.artifactVersion = artifactVersion
        tfsBuildDefinitionParams.inputs.repositoryName = repositoryName
        updateTFSBuild(idBuildPipelineTfs, tfsBuildDefinitionParams)

        when: 'the procedure runs'
        def urlLogList = runTfsPipeline(idBuildPipelineTfs)
        def listOfLinks = getTfsLogLink(urlLogList)
        def allLogs = getAllTfsLogs(listOfLinks)
        then: 'the procedure result validation'
        verifyAll {
            allLogs =~ logsToVerify
        }
        where: 'The following params will be: '
        testCaseID | artifactPath     | artifactName  | artifactVersion              | repositoryName | logsToVerify
        TC.C368148 | "nonexistent"    | "com:QArepo"  | "\$(Build.BuildNumber)"      | "default"      | "\\[error\\] No files was specified."
        TC.C368150 | "*"              | "nonexistent" | "\$(Build.BuildNumber)"      | "default"      | "\\[error\\] couldn't create artifact version: error \\[InvalidArtifactName\\]"
        TC.C368170 | "*"              | "com:QArepo"  | "<123>"                      | "default"      | "\\[error\\] couldn't create artifact version: error \\[InvalidVersion\\]: 'version' must be between 1 and 255 characters"
        TC.C368171 | "*"              | "com:QArepo"  | "\$(Build.BuildNumber)"      | "wrong"        | "\\[error\\]Repository 'wrong' does not exist"
    }

}
