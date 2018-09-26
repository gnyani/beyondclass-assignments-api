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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private Logger log = LoggerFactory.getLogger(OnlineCompilerRestController.class)

    @Value('${hacker.rank.api.key}')
    String api_key

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    static final int hardTimeout = 35 //seconds

    def slurper = new JsonSlurper()

    @ResponseBody
    @GetMapping(value = '/hackerrank/languages')
    public ResponseEntity<?> getlanguagecodes() {
        log.info("<OnlineCompilerRestController> getting language codes from hackerrank api")
        def json = new JsonSlurper().parseText(new URL("http://api.hackerrank.com/checker/languages.json").getText())
        new ResponseEntity<>(json, HttpStatus.OK)
    }

    @ResponseBody
    @PostMapping(value = '/hackerrank/submit')
    public def submitCode(@RequestBody CompilerInput compilerInput) {
        def testcases = [compilerInput.testcases.toString()]
        def testcasesJson = JsonOutput.toJson(testcases)
        log.info("<OnlineCompilerRestController> Code editor compile and run ${compilerInput.toString()}")
        HttpPost httpPost = new HttpPost("http://api.hackerrank.com/checker/submission.json");
        CloseableHttpClient client = HttpClients.createDefault()
        List<NameValuePair> params = new ArrayList<NameValuePair>()
        params.add(new BasicNameValuePair("source", "${compilerInput.source}"))
        params.add(new BasicNameValuePair("lang", "${compilerInput.lang}"))
        params.add(new BasicNameValuePair("api_key","${api_key}"))
        params.add(new BasicNameValuePair("testcases","${testcasesJson}"))
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse response = client.execute(httpPost);
        log.info("<OnlineCompilerRestController>status is " + response.statusLine)
        log.info("<OnlineCompilerRestController>response is" + response.getEntity())
        String responseString = new BasicResponseHandler().handleResponse(response)
        log.info(responseString)
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
            if(compilerInput.source.trim() != ""){
                responses[i] = submitAssignment(compilerInput)
            }
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
            log.info("<OnlineCompilerRestController> Assignment compile and run ${compilerInput.toString()}")
            def assignment = createAssignmentRepository.findByAssignmentid(compilerInput.assignmentid)
            def questionNumber = assignment.questions.findIndexOf { it == compilerInput.question }

            def testcases = assignment.inputs[questionNumber]
            def testcasesJson = JsonOutput.toJson(testcases)
            def expected = assignment.outputs[questionNumber]
            expected = expected.collect {
                removeUselessSpaces(it.trim())
            }

            HttpPost httpPost = new HttpPost("http://api.hackerrank.com/checker/submission.json");
            CloseableHttpClient client = HttpClients.createDefault()
            List<NameValuePair> params = new ArrayList<NameValuePair>()
            params.add(new BasicNameValuePair("source", "${compilerInput.source}"))
            params.add(new BasicNameValuePair("lang", "${compilerInput.lang}"))
            params.add(new BasicNameValuePair("api_key", "${api_key}"))
            params.add(new BasicNameValuePair("testcases", "${testcasesJson}"))
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            try{
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (httpPost != null) {
                            httpPost.abort()
                        }
                    }
                }
                new Timer(true).schedule(task, hardTimeout * 1000)
                CloseableHttpResponse response = client.execute(httpPost)
                log.info("<OnlineCompilerRestController>status is " + response.statusLine)
                log.info("<OnlineCompilerRestController>response is" + response.getEntity())
                String responseString = new BasicResponseHandler().handleResponse(response)

                def validation = validateAssignmentResult(expected, responseString)

                log.info("<OnlineCompilerRestController>Building response with ${validation} and from Hr is ${responseString} and ${expected}")
                def finalresponse = buildResponse(validation, responseString, expected, testcases, false)

                client.close()

                return  finalresponse
            } catch (SocketException e){
                log.info("<OnlineCompilerRestController> timeout issue due to infinite loop while compiling assignment")
                def finalresponse = buildResponse(false, null, null, null, true)
                finalresponse
            }
        }else{
            new ResponseEntity<>("source cant be empty",HttpStatus.BAD_REQUEST)
        }

    }

    def validateAssignmentResult(def expected, def response){


        def actualResponse = slurper.parseText(response)

        log.info("<OnlineCompilerRestController> assignment response is "+ actualResponse.result)

        def actual = actualResponse.result.stdout

        actual = actual.collect{
            removeUselessSpaces(it.trim())
        }

        log.info("<OnlineCompilerRestController> actual is  ${actual.toString()}")

        log.info("<OnlineCompilerRestController> expected is ${expected.toString()}")

        actual == expected
    }

    def removeUselessSpaces(String expected){

        log.info("<OnlineCompilerRestController> expected before trimming ${expected}")
        def splits = expected.split('\n')
        splits = splits.collect{it.trim()}
        expected = splits.join('\n')
        log.info("<OnlineCompilerRestController> expected after trimming ${expected}")
        expected
    }

    def buildResponse(Boolean validation, def response, def expected, def expectedInput, Boolean timeout){
        CodingAssignmentResponse codingAssignmentResponse = new CodingAssignmentResponse()
        if(timeout){
            codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.TIME_OUT
            codingAssignmentResponse.errorMessage = "Code took more than ${hardTimeout} seconds to execute, Sign of a possible infinite loop ?"
            return  codingAssignmentResponse
        }else{
            def jsonResponse = slurper.parseText(response)

            def message = jsonResponse.result.message

            if(validation){
                codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.SUCCESS
            }else{
                log.info("<OnlineCompilerRestController> Not success since expected and actual are not same now checking for compiler")
                if(jsonResponse.result.compilemessage.contains("error")) {
                    codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.COMPILER_ERROR
                    codingAssignmentResponse.errorMessage = jsonResponse.result.compilemessage
                }
                else if((message && message.contains("Runtime error")) || (message && message.contains("Segmentation Fault")) ){
                    codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.RUNTIME_ERROR
                    codingAssignmentResponse.errorMessage = jsonResponse.result.stderr
                    int index = message.findIndexOf{it == "Runtime error"} > 0 ? message.findIndexOf{it == "Runtime error"}: 0
                    codingAssignmentResponse.expectedInput = expectedInput[index]
                }else{
                    String[] actual = jsonResponse.result.stdout
                    actual = actual.collect{removeUselessSpaces(it.trim())}
                    int i = 0
                    def flag
                    for(int j=0; j< expected.size(); j++){
                        if(actual[i] != expected[j])
                        {
                            flag = true
                            break;
                        }
                        i++
                    }
                    if(flag){
                        codingAssignmentResponse.codingAssignmentStatus = CodingAssignmentStatus.TESTS_FAILED
                        codingAssignmentResponse.failedCase = i + 1
                        codingAssignmentResponse.totalCount = expected.size()
                        codingAssignmentResponse.passCount = i
                        codingAssignmentResponse.expected = expected[i]
                        codingAssignmentResponse.actual = actual[i]
                        codingAssignmentResponse.expectedInput = expectedInput[i]
                    }
                }
            }
            codingAssignmentResponse.runtime = jsonResponse.result.time
            codingAssignmentResponse.memory = jsonResponse.result.memory
            codingAssignmentResponse
        }
    }

}