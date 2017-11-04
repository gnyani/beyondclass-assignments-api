package com.engineeringeverything.Assignments.web.Controller


import api.compiler.CompilerInput
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovyx.net.http.Method
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

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
//        def http = new HTTPBuilder('http://api.hackerrank.com/checker/submission.json')
//        http.request( POST,URLENC ) { req ->
//            headers.Accept = 'application/json'
//            headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
//            body = [source: "${compilerInput.source}", lang: "${compilerInput.lang}", api_key: "${api_key}", testcases: "${testcasesJson}"]
//
//            response.success = { resp, reader ->
//                println "Got response: ${resp.statusLine}"
//                println "Content-Type: ${resp.headers.'Content-Type'}"
//                println(reader.getClass())
//                println(reader)
//            }}
//       }

        def post = Http.post(url, )
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");

    }

}