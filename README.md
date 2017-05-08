# ElectricFlow


ElectricFlow is an enterprise-grade DevOps Release Automation platform that simplifies provisioning, build and release of multi-tiered applications. Our model-driven approach to managing environments and applications allows teams to collaborate on and coordinate multiple pipelines and releases across hybrid infrastructure in an efficient, predictable and auditable way.


# Features
Key feature of integration includes:

* Run ElectricFlow Pipeline
* Publish Artifact from TFS into ElectricFlow

Following build actions are available in ElectricFlow Plugin. These actions can be executed separately or combined sequentially.

## Publish Artifact to ElectricFlow

This integration allows you to publish artifact for your application to ElectricFlow.

This build task has following parameters:

* Endpoint: ElectricFlow service endpoint
* Artifact Path: Location or path for the artifact files to be published to ElectricFlow. For e.g., MyProject/**/*-$BUILD_NUMBER.war
* Artifact Name: Name of the application artifact using the format <group_id>:<artifact_key>. For e.g., "com.example:helloworld"
* Artifact Version: Version of the application artifact. For e.g., you can specify 1.0 or 1.0-$(Build.BuildNumber) that is based on Visual Studio Build variable.
* ElectricFlow Repository Name: Name of the ElectricFlow Repository


## Run Pipeline in ElectricFlow

This integration allows you to run a pipeline in ElectricFlow.

This build task has following parameters:

* Endpoint: Name of the ElectricFlow configuration
* Project Name: Name of the ElectricFlow project
* Pipeline Name: Name of the ElectricFlow pipeline
(Optional) Pipeline Parameters

## Configurations

In order to use and integrate with ElectricFlow, you would have to create endpoints in TFS. Navigate to Services/Endpoints and find ElectricFlow service endpoint. One or more configurations can be created to connect to and call APIs into ElectricFlow system. For each endpoint, following attributes need to be specified:

* Connection Name: Name of the ElectricFlow endpoint
* Server URL: URL for the ElectricFlow Server. For e.g., https://<electric-flow-server>
* User Name: User name for the ElectricFlow
* User Password: User password for the ElectricFlow
* Accept untrusted SSL certificates: if checked, untrusted certificates will be accepted.



