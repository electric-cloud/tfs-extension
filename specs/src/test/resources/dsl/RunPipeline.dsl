project 'qaProject', {
  resourceName = null
  workspaceName = null

  pipeline 'qaPipeline', {
    description = ''
    enabled = '1'
    pipelineRunNameTemplate = null
    releaseName = null
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null

    formalParameter 'VAR1', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '1'
      required = '1'
      type = 'entry'
    }

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage1', {
      description = ''
      colorCode = '#00adee'
      completionType = 'auto'
      condition = null
      duration = null
      parallelToPrevious = null
      pipelineName = 'qaPipeline'
      plannedEndDate = null
      plannedStartDate = null
      precondition = null
      resourceName = ''
      waitForPlannedStartDate = '0'

      gate 'PRE', {
        condition = null
        precondition = null
      }

      gate 'POST', {
        condition = null
        precondition = null
      }

      task 'task1', {
        description = ''
        actualParameter = [
          'commandToRun': 'echo $[VAR1]',
        ]
        advancedMode = '0'
        afterLastRetry = null
        alwaysRun = '0'
        condition = null
        deployerExpression = null
        deployerRunType = null
        duration = null
        enabled = '1'
        environmentName = null
        environmentProjectName = null
        environmentTemplateName = null
        environmentTemplateProjectName = null
        errorHandling = 'stopOnError'
        gateCondition = null
        gateType = null
        groupName = null
        groupRunType = null
        insertRollingDeployManualStep = '0'
        instruction = null
        notificationEnabled = null
        notificationTemplate = null
        parallelToPrevious = null
        plannedEndDate = null
        plannedStartDate = null
        precondition = null
        requiredApprovalsCount = null
        resourceName = ''
        retryCount = null
        retryInterval = null
        retryType = null
        rollingDeployEnabled = null
        rollingDeployManualStepCondition = null
        skippable = '0'
        snapshotName = null
        stageSummaryParameters = null
        startingStage = null
        subErrorHandling = null
        subapplication = null
        subpipeline = null
        subpluginKey = 'EC-Core'
        subprocedure = 'RunCommand'
        subprocess = null
        subproject = null
        subrelease = null
        subreleasePipeline = null
        subreleasePipelineProject = null
        subreleaseSuffix = null
        subservice = null
        subworkflowDefinition = null
        subworkflowStartingState = null
        taskProcessType = null
        taskType = 'COMMAND'
        triggerType = null
        waitForPlannedStartDate = '0'
      }
    }
  }
}
