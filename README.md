# ElectricFlow


ElectricFlow is an enterprise-grade DevOps Release Automation platform that simplifies provisioning, build and release of multi-tiered applications. Our model-driven approach to managing environments and applications allows teams to collaborate on and coordinate multiple pipelines and releases across hybrid infrastructure in an efficient, predictable and auditable way.


# Features
Key feature of integration includes:

* Run ElectricFlow Pipeline
* Publish Artifact from Visual Studio Team Services/Team Foundation Server into ElectricFlow

Following build actions are available in ElectricFlow Plugin. These actions can be executed separately or combined sequentially.

## Publish Artifact to ElectricFlow

This integration allows you to publish artifact for your application to ElectricFlow.

![Publish Artifact](https://github.com/electric-cloud/tfs-extension/blob/master/Screenshots/PublishArtifact.png?raw=true)

This build task has following parameters:

* Endpoint: Endpoint URL of the ElectricFlow service
* Artifact Path: Location or path for the artifact files to be published to ElectricFlow. For e.g., MyProject/**/*-$BUILD_NUMBER.war
* Artifact Name: Name of the application artifact using the format `<group_id>:<artifact_key>`. For example, "com.example:helloworld"
* Artifact Version: Version of the application artifact. For e.g., you can specify 1.0 or 1.0-$(Build.BuildNumber) that is based on Visual Studio Build variable.
* ElectricFlow Repository Name: Name of the ElectricFlow Repository


## Run Pipeline in ElectricFlow

This integration allows you to run a pipeline in ElectricFlow.

![Run Pipeline](https://raw.githubusercontent.com/electric-cloud/tfs-extension/master/Screenshots/RunPipeline.png)

This build task has following parameters:

* Endpoint: Endpoint URL of the ElectricFlow service
* Project Name: Name of the ElectricFlow project
* Pipeline Name: Name of the ElectricFlow pipeline
* (Optional) Pipeline Parameters


## Run REST Call in ElectricFlow

This integration allows you to run a custom REST call in ElectricFlow.

![Run REST Call](https://raw.githubusercontent.com/electric-cloud/tfs-extension/master/Screenshots/RunRest.png)

This build task has following parameters:

* Endpoint: Endpoint URL of the ElectricFlow service
* Method: HTTP method to run
* Path: Path to the resource, e.g. /projects
* Parameters: Query parameters, in JSON or in key-value pairs.
* Payload: Enter request body.
* Result variable name: Enter variable name to store call results.


## Trigger Release in Electric Flow

This integration allows you to to start a release in ElectricFlow.

![Trigger Release](https://raw.githubusercontent.com/electric-cloud/tfs-extension/master/Screenshots/TriggerRelease.png)

This build task has following parameters:

* Endpoint: Endpoint URL of the ElectricFlow service
* Project Name: Name of the ElectricFlow project
* Release Name: Name of the release in ElectricFlow project.
* Starting stage name: Specify the starting stage name in release pipeline.
* Stages to run: One or more stages to run in a pipeline associated with the release, comma-separated.
* Requires pipeline parameters: If checked, parameters will be passed to the pipeline.
* Pipeline parameters: Parameters for the pipeline call, in key=value pairs or in JSON format. E.g. myParam = value.


## Service endpoints

![Endpoint](https://github.com/electric-cloud/tfs-extension/blob/master/Screenshots/Endpoint.png?raw=true)

In order to use and integrate with ElectricFlow, you would have to create endpoints in Visual Studio Team Services/Team Foundation Services. Navigate to Services/Endpoints and find ElectricFlow service endpoint. One or more configurations can be created to connect to and call APIs into ElectricFlow system. For each endpoint, following attributes need to be specified:

* Connection Name: Name of the ElectricFlow endpoint
* Server URL: URL for the ElectricFlow Server. For example, `https://<electric-flow-server>`
* REST API Version: Version for the ElectricFlow REST API. For e.g., v1
* User Name: User name for the ElectricFlow
* User Password: User password for the ElectricFlow
* Accept Untrusted SSL certificates: If checked, untrusted certificates will be accepted.

# Changelog

## Release 1.0

First public release

