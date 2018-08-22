package com.engineeringeverything.Assignments.web.Controller


import api.compiler.CompilerInput
import api.createassignment.CodingAssignmentResponse
import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignment
import api.submitassignment.SubmitProgrammingAssignmentRequest
import api.submitassignment.AssignmentSubmissionStatus
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import constants.CodingAssignmentStatus
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
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    def slurper = new JsonSlurper()

    @ResponseBody
    @GetMapping(value = '/hackerrank/languages')
    public ResponseEntity<?> getlanguagecodes() {
        def json = new JsonSlurper().parseText(new URL("http://api.hackerrank.com/checker/languages.json").getText())
        new ResponseEntity<>(json, HttpStatus.OK)
    }

    @ResponseBody
    @PostMapping(value = '/hackerrank/submit')
    public def submitCode(@RequestBody CompilerInput compilerInput) {
        def testcases = [compilerInput.testcases.toString()]
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


    @ResponseBody
    @PostMapping(value='/hackerrank/assignment/submit')
    public ResponseEntity<?> submitAssign(@RequestBody SubmitProgrammingAssignmentRequest programmingAssignment){

        def user = serviceUtilities.findUserByEmail(programmingAssignment.email)
        SubmitAssignment submitProgrammingAssignment = new SubmitAssignment()
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(programmingAssignment.tempassignmentid)

        CodingAssignmentResponse[] responses = new CodingAssignmentResponse[programmingAssignment.questions.size()]


        for(int i=0;i< programmingAssignment.questions.size();i++)
        {
            CompilerInput compilerInput = new CompilerInput()
            compilerInput.with {
                source = programmingAssignment.source[i]
                lang = programmingAssignment.langcode[i]
                question = programmingAssignment.questions[i]
                assignmentid = programmingAssignment.tempassignmentid
            }
            if(compilerInput.source.trim() != "")
            responses[i] = submitAssignment(compilerInput)
            else{
                CodingAssignmentResponse codingAssignmentResponse = new CodingAssignmentResponse()
                codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.RUNTIME_ERROR
                codingAssignmentResponse.errorMessage = 'Source Code cannot be empty'
                responses[i] = codingAssignmentResponse
            }
        }

        submitProgrammingAssignment.with {
            mode = programmingAssignment.language
            email = programmingAssignment.email
            username = serviceUtilities.generateUserName(user)
            rollnumber = user ?. rollNumber
            answers = programmingAssignment.source
            timespent = programmingAssignment.timespent
            questionIndex = createAssignment.studentQuestionMapping.get(programmingAssignment.email)
            propicurl =  user ?. normalpicUrl ?: user ?. googlepicUrl
            status = AssignmentSubmissionStatus.PENDING_APPROVAL
            codingAssignmentResponse = responses
            tempassignmentid = serviceUtilities.generateFileName(programmingAssignment.tempassignmentid,programmingAssignment.email)
        }

        def currentSubmittedStudents =  createAssignment.getSubmittedstudents()
        def submittedDates = createAssignment.getSubmittedDates()
        if(currentSubmittedStudents) {
            currentSubmittedStudents.add(programmingAssignment.email)
            submittedDates.put(programmingAssignment.email, new Date())
        }
        else
        {
            currentSubmittedStudents = new HashSet<String>()
            currentSubmittedStudents.add(programmingAssignment.email)
            submittedDates = new HashMap<String, Date>()
            submittedDates.put(programmingAssignment.email,new Date())
        }
        createAssignment.setSubmittedstudents(currentSubmittedStudents)
        createAssignment.setSubmittedDates(submittedDates)
        CreateAssignment createAssignment1 = createAssignmentRepository.save(createAssignment)

        SubmitAssignment submitProgrammingAssignment1 = submitAssignmentRepository.save(submitProgrammingAssignment)
        submitProgrammingAssignment1 && createAssignment1 ? new ResponseEntity<>("Saved successfully",HttpStatus.OK) : new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value = '/hackerrank/assignment/compile')
    public def submitAssignment(@RequestBody CompilerInput compilerInput){

        if(compilerInput.source.trim() != "") {

            def assignment = createAssignmentRepository.findByAssignmentid(compilerInput.assignmentid)
            def questionNumber = assignment.questions.findIndexOf { it == compilerInput.question }

            def testcases = assignment.inputs[questionNumber]
            def testcasesJson = JsonOutput.toJson(testcases)
            def expected = assignment.outputs[questionNumber]
            expected = expected.collect { it.trim() }

            HttpPost httpPost = new HttpPost("http://api.hackerrank.com/checker/submission.json");
            CloseableHttpClient client = HttpClients.createDefault()
            List<NameValuePair> params = new ArrayList<NameValuePair>()
            params.add(new BasicNameValuePair("source", "${compilerInput.source}"))
            params.add(new BasicNameValuePair("lang", "${compilerInput.lang}"))
            params.add(new BasicNameValuePair("api_key", "${api_key}"))
            params.add(new BasicNameValuePair("testcases", "${testcasesJson}"))
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse response = client.execute(httpPost);
            println("status is " + response.statusLine)
            println("response is" + response.getEntity())
            String responseString = new BasicResponseHandler().handleResponse(response)

            def validation = validateAssignmentResult(expected, responseString)

            println("Building response with ${validation} and from Hr is ${responseString} and ${expected}")
            def finalresponse = buildResponse(validation, responseString, expected)

            client.close()

            finalresponse
        }else{
            new ResponseEntity<>("source cant be empty",HttpStatus.BAD_REQUEST)
        }

    }

    def validateAssignmentResult(def expected, def response){


        def actualResponse = slurper.parseText(response)

        println("response is "+ actualResponse.result)

        def actual = actualResponse.result.stdout

        actual = actual.collect{it.trim()}

        println("actual is ${actual}")

        println("expected is ${expected}")

        actual == expected
    }

    def buildResponse(Boolean validation, def response, def expected){

        CodingAssignmentResponse codingAssignmentResponse = new CodingAssignmentResponse()

        def jsonResponse = slurper.parseText(response)

        if(validation){
            codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.SUCCESS
        }else{
            if(jsonResponse.result.compilemessage != '') {
                codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.COMPILER_ERROR
                codingAssignmentResponse.errorMessage = jsonResponse.result.compilemessage
            }
            else if(jsonResponse.result.message && jsonResponse.result.message[0] == "Runtime error"){
                codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.RUNTIME_ERROR
                codingAssignmentResponse.errorMessage = jsonResponse.result.stderr[0]
            }else{
                String[] actual = jsonResponse.result.stdout
                int i = 0
                def flag
                 expected.each{
                     if( actual == null || actual[i] != it)
                     {
                         flag = true
                         return
                     }
                     i++
                 }
                if(flag){
                    codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.TESTS_FAILED
                    codingAssignmentResponse.failedCase = i + 1
                    codingAssignmentResponse.totalCount = expected.size()
                    codingAssignmentResponse.passCount = i > 0 ? i-1 : i
                    codingAssignmentResponse.expected = expected[i]
                    codingAssignmentResponse.actual = actual[i]
                }
            }
        }
        codingAssignmentResponse.runtime = jsonResponse.result.time
        codingAssignmentResponse.memory = jsonResponse.result.memory
        codingAssignmentResponse
    }

}