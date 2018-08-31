package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.createassignment.TeacherAssignmentList
import api.evaluateassignment.AssignmentQuestionsAndAnswers
import api.evaluateassignment.AssignmentSubmissionDetails
import api.saveassignment.ReturnSavedAssignment
import api.saveassignment.SaveAssignment
import api.saveassignment.SaveObjectiveAssignment
import api.saveassignment.SaveProgrammingAssignment
import api.submitassignment.SubmitAssignment
import api.user.User
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveCreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveObjectiveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveProgrammingAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.GetQuestions
import com.engineeringeverything.Assignments.core.Service.PDFGenerator
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.web.Converter.ObjectConverter
import constants.AssignmentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse
import java.nio.file.Files
import java.nio.file.Path


/**
 * Created by GnyaniMac on 02/10/17.
 */

@RestController
class ListAssignmentsRestController {

    private Logger log = LoggerFactory.getLogger(ListAssignmentsRestController.class)

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    SaveAssignmentRepository saveAssignmentRepository

    @Autowired
    SaveProgrammingAssignmentRepository saveProgrammingAssignmentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    @Autowired
    SaveCreateAssignmentRepository saveCreateAssignmentRepository

    @Autowired
    ObjectConverter createAssignmentConverter

    @Autowired
    PDFGenerator pdfGenerator

    @Autowired
    GetQuestions getQuestions

    @Autowired
    SaveObjectiveAssignmentRepository saveObjectiveAssignmentRepository

    @ResponseBody
    @PostMapping(value = '/teacher/list')
    public ResponseEntity<?> listAssignments (@RequestBody TeacherAssignmentList teacherAssignmentList){

        log.info("<ListAssignmentsRestController> list the assignments for teacher ${teacherAssignmentList.email}")

        def uniqueClassId = serviceUtilities.generateUniqueClassIdForTeacher(teacherAssignmentList.batch, teacherAssignmentList.email)

        def assignmentid = serviceUtilities.generateFileName(uniqueClassId,teacherAssignmentList.email)

        def list = createAssignmentRepository.findByAssignmentidStartingWithOrderByCreateDateDesc(assignmentid)

        def convertedList = []

        list.each {
            convertedList << createAssignmentConverter.convertToListCreateAssignment(it)
        }

        list ? new ResponseEntity<>(convertedList,HttpStatus.OK): new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }



    @ResponseBody
    @PostMapping(value = '/teacher/getquestions')
    public ResponseEntity<?> getQuestions(@RequestBody String assignmentid){

        log.info("<ListAssignmentsRestController> fetching questions for a given assignment id ${assignmentid}")

        def assignment = createAssignmentRepository.findByAssignmentid(assignmentid)

        assignment ? new ResponseEntity<>(assignment.questions,HttpStatus.OK) : new ResponseEntity<>("record not found",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value = '/teacher/saved/list')
    public ResponseEntity<?> listSavedAssignments (@RequestBody TeacherAssignmentList teacherAssignmentList){

        log.info("<ListAssignmentsRestController> fetching saved assignments for teacher ${teacherAssignmentList.email}")

        def uniqueClassId = serviceUtilities.generateUniqueClassIdForTeacher(teacherAssignmentList.batch,teacherAssignmentList.email)
        def assignmentid = serviceUtilities.generateFileName(uniqueClassId, teacherAssignmentList.email)
        def list = saveCreateAssignmentRepository.findByAssignmentidStartingWithOrderByCreateDateDesc(assignmentid)
        list ? new ResponseEntity<>(list,HttpStatus.OK): new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/student/list')
    public ResponseEntity<?> listStudentAssignments (@RequestBody String email){

        log.info("<ListAssignmentsRestController> fetching pending assignments list for student ${email}")

        def user = serviceUtilities.findUserByEmail(email)
        def assignmentid = serviceUtilities.generateFileName(user.university,user.college,user.branch,user.section,user.startYear,user.endYear)
        def list = createAssignmentRepository.findByAssignmentidStartingWithAndSubmittedstudentsNotContainingAndLastdateAfterOrderByLastdate(assignmentid,email,new Date()-1)
        def convertedList = []

        list.each {
            convertedList << createAssignmentConverter.convertToListCreateAssignment(it)
        }
        list ? new ResponseEntity<>(convertedList,HttpStatus.OK): new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/get/{assignmentId:.+}')
    public ResponseEntity<?> fetchAssignment(@PathVariable(value="assignmentId" , required = true) String assignmentId,@RequestBody String email){

        log.info("<ListAssignmentsRestController> fetching saved assignment for student ${email} and assignment id is ${assignmentId}")

        CreateAssignment assignment = createAssignmentRepository.findByAssignmentid(assignmentId)

        Boolean valid = isValidSubmission(assignment,email)
        if(valid) {
            ReturnSavedAssignment returnSavedAssignment = new ReturnSavedAssignment()
            Object[] questions = genrateRandomQuestionsForStudent(assignment, email)
            returnSavedAssignment.setQuestions(questions)
            returnSavedAssignment.setAssignmentType(assignment?.assignmentType)
            if (assignment.assignmentType == AssignmentType.THEORY) {
                SaveAssignment saveAssignment = saveAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentId, email))
                returnSavedAssignment.setAnswers(saveAssignment?.answers)
                returnSavedAssignment.setAnswersContentStates(saveAssignment ?.answersContentStates )
                if (saveAssignment?.timespent != null)
                    returnSavedAssignment.setTimespent(saveAssignment?.timespent)
                return assignment ? new ResponseEntity<>(returnSavedAssignment, HttpStatus.OK) : new ResponseEntity<>("no records found", HttpStatus.NO_CONTENT)
            } else if (assignment.assignmentType == AssignmentType.CODING) {

                SaveProgrammingAssignment saveProgrammingAssignment = saveProgrammingAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentId, email))
                returnSavedAssignment.setSource(saveProgrammingAssignment?.source)
                returnSavedAssignment.setLanguage(saveProgrammingAssignment?.language)
                returnSavedAssignment.setLangCodes(saveProgrammingAssignment?.langCodes)
                returnSavedAssignment.setTheme(saveProgrammingAssignment?.theme)
                if (saveProgrammingAssignment?.timespent != null)
                    returnSavedAssignment.setTimespent(saveProgrammingAssignment?.timespent)
                return assignment ? new ResponseEntity<>(returnSavedAssignment, HttpStatus.OK) : new ResponseEntity<>("no records found", HttpStatus.NO_CONTENT)
            } else if (assignment.assignmentType == AssignmentType.OBJECTIVE) {
                SaveObjectiveAssignment saveObjectiveAssignment = saveObjectiveAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentId, email))
                returnSavedAssignment.setUserValidity(saveObjectiveAssignment?.getUserValidity())
                returnSavedAssignment.setOptions(getOptionsOfQuestion(assignment,email))
                returnSavedAssignment.setValidity(getValidityOfQuestion(assignment,email))
                if (saveObjectiveAssignment?.getTimespent() != null)
                    returnSavedAssignment.setTimespent(saveObjectiveAssignment.getTimespent())
                return assignment ? new ResponseEntity<>(returnSavedAssignment, HttpStatus.OK) : new ResponseEntity<>("no records found", HttpStatus.NO_CONTENT)
            }
        }
    }

    @ResponseBody
    @GetMapping(value = '/get/assignmenttype/{assignmentId:.+}')
    public def getAssignmentType(@PathVariable(value="assignmentId" , required = true) String assignmentId){

        CreateAssignment assignment = createAssignmentRepository.findByAssignmentid(assignmentId)

        assignment ? new ResponseEntity<?>(assignment.assignmentType,HttpStatus.OK) : new ResponseEntity<?>("not found", HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/evaluate')
    public ResponseEntity<?> evaluate(@RequestBody AssignmentSubmissionDetails assignmentSubmissionDetails ){

        def (AssignmentQuestionsAndAnswers assignmentQuestionsAndAnswers, CreateAssignment createAssignment1, SubmitAssignment submitAssignment1) = fetchQuestionsAndAnswers(assignmentSubmissionDetails)
        createAssignment1 && submitAssignment1 ? new ResponseEntity<>(assignmentQuestionsAndAnswers,HttpStatus.OK) : new ResponseEntity<>('Something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)

    }

    private List fetchQuestionsAndAnswers(AssignmentSubmissionDetails assignmentSubmissionDetails) {
        AssignmentQuestionsAndAnswers assignmentQuestionsAndAnswers = new AssignmentQuestionsAndAnswers()
        CreateAssignment createAssignment1 = createAssignmentRepository.findByAssignmentid(assignmentSubmissionDetails.assignmentid)
        SubmitAssignment submitAssignment1 = submitAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentSubmissionDetails.assignmentid, assignmentSubmissionDetails.email))

        def user = userRepository.findByEmail(assignmentSubmissionDetails.email)
        def questions = getQuestionsOfStudent(createAssignment1, assignmentSubmissionDetails.email)

        assignmentQuestionsAndAnswers.with {
            createAssignment = createAssignmentConverter.convertToCreateAssignmentResponse(createAssignment1)
            submitAssignment = submitAssignment1
            timespent = formatDuration(submitAssignment1.timespent)
            submittedQuestions = questions
            userName = user?.firstName?.capitalize() + ' ' + user?.lastName?.capitalize()
            rollNumber = user.rollNumber
        }
        if (createAssignment1.assignmentType == AssignmentType.OBJECTIVE) {
            assignmentQuestionsAndAnswers.createAssignment.options = getOptionsOfQuestion(createAssignment1, assignmentSubmissionDetails.email)
            assignmentQuestionsAndAnswers.createAssignment.validity = getValidityOfQuestion(createAssignment1, assignmentSubmissionDetails.email)
        }
        [assignmentQuestionsAndAnswers, createAssignment1, submitAssignment1]
    }

    @ResponseBody
    @GetMapping(value = '/get/submission/{submissionId:.+}',produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> generateSubmissionPDF(@PathVariable(value="submissionId" , required = true) String submissionId,HttpServletResponse response) {

        AssignmentSubmissionDetails assignmentSubmissionDetails = new AssignmentSubmissionDetails()

        def splits = submissionId.tokenize('*')
        assignmentSubmissionDetails.with {
            assignmentid = splits[0]
            email = splits[1]
        }

        def (AssignmentQuestionsAndAnswers assignmentQuestionsAndAnswers) = fetchQuestionsAndAnswers(assignmentSubmissionDetails)
        assignmentQuestionsAndAnswers.submittedQuestions = getQuestions.parseQuestions(assignmentQuestionsAndAnswers.submittedQuestions)

        Path submissionPDF = pdfGenerator.createSubmissionPDF(assignmentQuestionsAndAnswers)
        byte[] file = Files.readAllBytes(submissionPDF)

        response.setContentType("application/pdf;charset=UTF-8");
        response.setHeader("Content-disposition",
                "inline; filename=\"" + assignmentQuestionsAndAnswers.userName+'('+assignmentQuestionsAndAnswers.rollNumber+')'+'submission.pdf' + "\"")

        Files.delete(submissionPDF)

        file? new ResponseEntity<>(file,HttpStatus.OK) : new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    public static String formatDuration(long millis) {
        long second = (long)(millis / 1000) % 60
        long minute = (long)(millis / (1000 * 60)) % 60
        long hour = (long)(millis / (1000 * 60 * 60)) % 24

        String.format("%02d:%02d:%02d", hour, minute, second)
    }


    Object[] genrateRandomQuestionsForStudent(CreateAssignment createAssignment,String email){

        def questions = []

        if(createAssignment.studentQuestionMapping ?. get(email) != null)
        {
            questions = getQuestionsOfStudent(createAssignment, email)
        }
        else {
            def max = createAssignment.questions.size()
            Random rand = new Random()

            def randList = []

            while(randList.size() < createAssignment.numberOfQuesPerStudent){
                int randNum = rand.nextInt(max)
                if(!randList.contains(randNum))
                    randList << randNum
            }

            (0..createAssignment.numberOfQuesPerStudent-1).each {
                int randNum = randList.get(it).toString().toInteger()
                questions << createAssignment.questions[randNum]
            }

            if(createAssignment.studentQuestionMapping == null) {
                def map = new HashMap()
                map.put(email, randList)
                createAssignment.setStudentQuestionMapping(map)
            }else{
                def map = createAssignment.studentQuestionMapping
                map.put(email, randList)
                createAssignment.setStudentQuestionMapping(map)
            }

            createAssignmentRepository.save(createAssignment)
        }
     questions
    }

    private Object[] getQuestionsOfStudent(CreateAssignment createAssignment, String email) {
        def questionList = []
        def randList = createAssignment.studentQuestionMapping.get(email)
        (0..createAssignment.numberOfQuesPerStudent - 1).each {
            int randNum = randList.get(it).toString().toInteger()
            questionList << createAssignment.questions[randNum]
        }
        questionList
    }

    private List<String[]> getOptionsOfQuestion(CreateAssignment createAssignment, String email) {
        def optionsList = []
        def randList = createAssignment.studentQuestionMapping.get(email)
        (0..createAssignment.numberOfQuesPerStudent - 1).each {
            int randNum = randList.get(it).toString().toInteger()
            optionsList << createAssignment.options[randNum]
        }
        optionsList
    }

    private List<int[]> getValidityOfQuestion(CreateAssignment createAssignment, String email) {
        def validityList = []
        def randList = createAssignment.studentQuestionMapping.get(email)
        (0..createAssignment.numberOfQuesPerStudent - 1).each {
            int randNum = randList.get(it).toString().toInteger()
            validityList << createAssignment.validity[randNum]
        }
        validityList
    }

    private Boolean isValidSubmission(CreateAssignment assignment, String email){
        Boolean valid = true
        Date currentDate = new Date()
        if((currentDate-1 > assignment.lastdate && currentDate.date != assignment.lastdate.date) || assignment?.submittedstudents?.contains(email)){
             valid = false
        }
        valid
    }
}
