package com.electriccloud.plugin.spec

import spock.lang.*
import com.electriccloud.spec.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class RunPipeline extends PluginTestHelper {

    def "RunPipeline, only required fields filled - Sanity"(){
        when: 'the procedure runs'
            //System.setProperty("httpsProxyHost","127.0.0.1");
            //System.setProperty("httpsProxyPort","8080");
            
            def result = null

            //-------------------------------------------------

            // Run TFS/AzureDevOps Pipeline
            // https://dev.azure.com/{organization}/{project}/_apis/build/builds?api-version=4.1
            def r = http.request( POST, JSON ) {
                uri.path = '/pluginsdev/Andrii Extension Test/_apis/build/builds'
                uri.query = ['api-version' : '4.1']
                println uri.toString() 
                body = '{ "definition": {"id":22 }}'
                headers.'Authorization' = authHeaderValue
                headers.'Content-Type' = 'application/json'

                response.success = { resp, json ->
                    println "LOG URL $json.logs.url"
                    result = json.logs.url
                }
            }
            def urlLogList = r;

            //-------------------------------------------------

            // We need wait until pipeline will be executed
            // Can we use long-polling or requests in loop?
            println "SLEEP"
            // Sleep 60 sec
            sleep(60000)

            //--------------------------------------------------

            // Get links to logs
            r = http.request( urlLogList, GET, JSON ) {
                println uri.toString() 
                headers.'Authorization' = authHeaderValue
                response.success = { resp, json ->
                    return json.get('value')
                }
            }

            //----------------------------------------------------
            def ef_pipeline_id = null
            println "R="
            println r
            for (def urlItem : r) {
                ef_pipeline_id = http.request( urlItem.url, GET, JSON ) {
                    println uri.toString() 
                    headers.'Authorization' = authHeaderValue

                    response.success = { resp, json ->
                        //println json
                        String log = json.get('value')
                        //if( !( log =~ /ElectricFlow - Call REST Endpoint/ ) ) {
                        if( !( log =~ /ElectricFlow - Run Pipeline/ ) ) {
                            return null
                        }

                        //def finder = log =~ /(?<=pipelineId: ')([\d,a-f,-]+)(?=')/
                        def finder = log =~ /Link to the pipeline runtime: .*\/flow\/#pipeline-run\/.+\/([\d,a-f,-]+),/
                        if( !finder) {
                            return null
                        }
                        if( !finder.find(1) ) {
                            return null
                        }
                        return finder.group(1)
                    }
                }

                if( ef_pipeline_id != null ) {
                    break;
                }
            }
            println "EF PIPELINE $ef_pipeline_id"
            assert ef_pipeline_id
            println "SLEEP"
            sleep(60000)

        then: 'the procedure result validation'            
            waitUntil {
                flowRuntimeDetails(ef_pipeline_id)
            }
            def status = flowRuntimeDetails(ef_pipeline_id)
            assert status.completed == "1"
        where:
            id << [1]
    }
}
