
project 'qaProject', {
  resourceName = null
  workspaceName = null

  pipeline 'qaPipelineNoVar', {
    description = ''
    enabled = '1'
    pipelineRunNameTemplate = null
    releaseName = null
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null

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
      pipelineName = 'qaPipelineNoVar'
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
          'commandToRun': 'echo Test',
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

  pipeline 'qaPipelineSpecialSymbols\u1d83\u1d84\u1d86\u1d87\u1d88\u1d89\u1d8a\u1d8b]', {
    description = ''
    enabled = '1'
    pipelineRunNameTemplate = null
    releaseName = null
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null

    formalParameter '\u1d83\u1d84\u1d86\u1d87\u1d88\u1d89\u1d8a\u1d8b]', defaultValue: null, {
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
      pipelineName = 'qaPipelineSpecialSymbols\u1d83\u1d84\u1d86\u1d87\u1d88\u1d89\u1d8a\u1d8b]'
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
          'commandToRun': 'echo $[\u1d83\u1d84\u1d86\u1d87\u1d88\u1d89\u1d8a\u1d8b]]',
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

  pipeline 'qaPipelineVar', {
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
      pipelineName = 'qaPipelineVar'
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

  pipeline 'qaPipelineVars', {
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

    formalParameter 'VAR2', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '2'
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
      pipelineName = 'qaPipelineVars'
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

      task 'task2', {
        description = ''
        actualParameter = [
          'commandToRun': 'echo $[VAR2]',
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

  release 'qaReleaseNoVar', {
    description = ''
    plannedEndDate = '2020-01-02'
    plannedStartDate = '2018-12-20'

    pipeline 'pipeline_qaReleaseNoVar', {
      enabled = '1'
      pipelineRunNameTemplate = null
      releaseName = 'qaReleaseNoVar'
      templatePipelineName = null
      templatePipelineProjectName = null
      type = null

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
        pipelineName = 'pipeline_qaReleaseNoVar'
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
            'commandToRun': 'echo task1',
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

      stage 'Stage2', {
        description = ''
        colorCode = '#00adee'
        completionType = 'auto'
        condition = null
        duration = null
        parallelToPrevious = null
        pipelineName = 'pipeline_qaReleaseNoVar'
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

        task 'task2', {
          description = ''
          actualParameter = [
            'commandToRun': 'echo task2',
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
    stage 'Stage3', {
        description = ''
        colorCode = '#00adee'
        completionType = 'auto'
        condition = null
        duration = null
        parallelToPrevious = null
        pipelineName = 'pipeline_qaReleaseNoVar'
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
            'commandToRun': 'echo task1',
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
    stage 'Stage4', {
        description = ''
        colorCode = '#00adee'
        completionType = 'auto'
        condition = null
        duration = null
        parallelToPrevious = null
        pipelineName = 'pipeline_qaReleaseNoVar'
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
            'commandToRun': 'echo task1',
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

  release 'qaReleaseVars', {
    description = ''
    plannedEndDate = '2019-01-03'
    plannedStartDate = '2018-12-20'

    pipeline 'pipeline_qaReleaseNoVar', {
      enabled = '1'
      pipelineRunNameTemplate = null
      releaseName = 'qaReleaseVars'
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

      formalParameter 'VAR2', defaultValue: null, {
        expansionDeferred = '0'
        label = null
        orderIndex = '2'
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
        pipelineName = 'pipeline_qaReleaseNoVar'
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
            'commandToRun': 'echo ${VAR1}',
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

      stage 'Stage2', {
        description = ''
        colorCode = '#00adee'
        completionType = 'auto'
        condition = null
        duration = null
        parallelToPrevious = null
        pipelineName = 'pipeline_qaReleaseNoVar'
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

        task 'task2', {
          description = ''
          actualParameter = [
            'commandToRun': 'echo ${VAR2}',
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

}
