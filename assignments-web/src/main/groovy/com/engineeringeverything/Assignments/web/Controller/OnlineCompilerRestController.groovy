package com.engineeringeverything.Assignments.web.Controller


import api.compiler.CompilerInput
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GnyaniMac on 29/10/17.
 */

@RestController
class OnlineCompilerRestController {

    @Value('${hacker.rank.api.key}')
    String api_key

    @ResponseBody
    @GetMapping(value = '/hackerrank/languages')
    public ResponseEntity<?> getlanguagecodes() {
        def json = new JsonSlurper().parseText(new URL("http://api.hackerrank.com/checker/languages.json").getText())
        new ResponseEntity<>(json, HttpStatus.OK)
    }

    @ResponseBody
    @PostMapping(value = '/hackerrank/submit')
    public def submitCode(@RequestBody CompilerInput compilerInput) {
        def testcases = compilerInput.testcases.split(" |\\n")
        def testcasesJson = JsonOutput.toJson(testcases)
        println(compilerInput.toString())
        HttpPost httpPost = new HttpPost("http://api.hackerrank.com/checker/submission.json");
        CloseableHttpClient client = HttpClients.createDefault()
        List<NameValuePair> params = new ArrayList<NameValuePair>()
        params.add(new BasicNameValuePair("source", "${compilerInput.source}"))
        params.add(new BasicNameValuePair("lang", "${compilerInput.lang}"))
        params.add(new BasicNameValuePair("api_key","${api_key}"))
        params.add(new BasicNameValuePair("testcases","${testcasesJson}"))
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(httpPost);
        println("status is " + response.statusLine)
        println("respons is" + response.getEntity())
        String responseString = new BasicResponseHandler().handleResponse(response)
        System.out.println(responseString)
        client.close()
        responseString
    }

}