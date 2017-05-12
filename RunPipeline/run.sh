#!/usr/bin/env bash

tsc

export INPUT_electricFlowService=testid
export ENDPOINT_URL_testid=http://rhel-oracle/rest/v1.0
export INPUT_skipCertCheck=true
export INPUT_projectName=Default
export INPUT_repositoryName=default
export INPUT_artifactVersion=1.0.6
export INPUT_resultVarName=test
export INPUT_artifactName=com.company:artifact
export INPUT_pipelineName="Test TFS"

node index.js
