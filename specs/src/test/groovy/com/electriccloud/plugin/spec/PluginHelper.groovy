package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import groovy.json.JsonSlurper
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class PluginTestHelper extends PluginSpockTestSupport {
    static final String PLUGIN_NAME = 'TFS_extension'
    
    static def         CONFIG_CREATED = false
    static HTTPBuilder http
    static String      authHeaderValue

    static def automationTestsContextRun = System.getenv ('AUTOMATION_TESTS_CONTEXT_RUN') ?: ''

    static String getHost() {
        def host = System.getenv('TFS_HOST')
        assert host
        return host
    }

    static String getToken() {
        def token = System.getenv('TFS_TOKEN')
        assert token
        return token
    }

    def setup() {
        this.http = new HTTPBuilder(getHost())
        //http.setProxy(
        //    '127.0.0.1',
        //    8080,
        //    'http'
        //)

        // Doesn't work for me
        this.http.ignoreSSLIssues()
        def authData = ':' + getToken()
        this.authHeaderValue = "Basic ${authData.bytes.encodeBase64().toString()}"
        println "Authorization: $authHeaderValue"

        // Default error handler
        http.handler.failure = { resp ->
            println "Unexpected failure: ${resp.statusLine}"
            println "Code: ${resp.status}"
            println "Body: ${resp.data}"
            println http.dump()
            println "-------------------"
            println resp.dump()
            println "-------------------"
            def result = resp
            println "========================="
        }
    }


    def createConfig(String configName) {
        createPluginConfiguration(
            pluginName,
            configName,
            [
                host: getHost(),
            ],
            getUsername(),
            getPassword(),
            [
                confPath: 'ServiceNow_cfgs',
                recreate: true,
            ]
        )
    }

    def deleteConfig() {
        deleteConfiguration(pluginName, getConfigName())
    }

    def conditionallyDeleteProject(String projectName){
        if (System.getenv("LEAVE_TEST_PROJECTS")){
            return
        }
        dsl "deleteProject projectName: '$projectName'"
    }
}

