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
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveCreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveObjectiveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveProgrammingAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.web.Converter.CreateAssignmentConverter
import constants.AssignmentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


/**
 * Created by GnyaniMac on 02/10/17.
 */

@RestController
class ListAssignmentsRestController {

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
    CreateAssignmentConverter createAssignmentConverter

    @Autowired
    SaveObjectiveAssignmentRepository saveObjectiveAssignmentRepository

    @ResponseBody
    @PostMapping(value = '/teacher/list')
    public ResponseEntity<?> listAssignments (@RequestBody TeacherAssignmentList teacherAssignmentList){

        def splits = teacherAssignmentList.batch.split('-')
        String startyear = splits[0]
        String section = splits[1]
        String endyear = Integer.parseInt(startyear)+ 4
        def user = serviceUtilities.findUserByEmail(teacherAssignmentList.email)

        def assignmentid = serviceUtilities.generateFileName(user.university,user.college,user.branch,section,startyear,endyear,teacherAssignmentList.email)

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

        def assignment = createAssignmentRepository.findByAssignmentid(assignmentid)

        assignment ? new ResponseEntity<>(assignment.questions,HttpStatus.OK) : new ResponseEntity<>("record not found",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value = '/teacher/saved/list')
    public ResponseEntity<?> listSavedAssignments (@RequestBody TeacherAssignmentList teacherAssignmentList){
        def splits = teacherAssignmentList.batch.split('-')
        String startyear = splits[0]
        String section = splits[1]
        String endyear = Integer.parseInt(startyear)+ 4
        def user = serviceUtilities.findUserByEmail(teacherAssignmentList.email)
        def assignmentid = serviceUtilities.generateFileName(user.university,user.college,user.branch,section,startyear,endyear,teacherAssignmentList.email)
        def list = saveCreateAssignmentRepository.findByAssignmentidStartingWithOrderByCreateDateDesc(assignmentid)
        list ? new ResponseEntity<>(list,HttpStatus.OK): new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/student/list')
    public ResponseEntity<?> listStudentAssignments (@RequestBody String email){

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

        CreateAssignment assignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        System.err.println(assignmentId);

        Boolean valid = isValidSubmission(assignment,email)
        if(valid) {
            ReturnSavedAssignment returnSavedAssignment = new ReturnSavedAssignment()
            returnSavedAssignment.setAssignmentType(assignment.assignmentType)
            Object[] questions = genrateRandomQuestionsForStudent(assignment, email)
            returnSavedAssignment.setQuestions(questions)
            returnSavedAssignment.setQuestions(assignment?.getQuestions())
            returnSavedAssignment.setAssignmentType(assignment?.assignmentType)

            if (assignment.assignmentType == AssignmentType.THEORY) {
                SaveAssignment saveAssignment = saveAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentId, email))
                returnSavedAssignment.setAnswers(saveAssignment?.answers)
                if (saveAssignment?.timespent != null)
                    returnSavedAssignment.setTimespent(saveAssignment?.timespent)
                return assignment ? new ResponseEntity<>(returnSavedAssignment, HttpStatus.OK) : new ResponseEntity<>("no records found", HttpStatus.NO_CONTENT)
            } else if (assignment.assignmentType == AssignmentType.CODING) {

                SaveProgrammingAssignment saveProgrammingAssignment = saveProgrammingAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentId, email))
                returnSavedAssignment.setSource(saveProgrammingAssignment?.source)
                returnSavedAssignment.setLanguage(saveProgrammingAssignment?.language)
                returnSavedAssignment.setTheme(saveProgrammingAssignment?.theme)
                if (saveProgrammingAssignment?.timespent != null)
                    returnSavedAssignment.setTimespent(saveProgrammingAssignment?.timespent)
                return assignment ? new ResponseEntity<>(returnSavedAssignment, HttpStatus.OK) : new ResponseEntity<>("no records found", HttpStatus.NO_CONTENT)
            } else if (assignment.assignmentType == AssignmentType.OBJECTIVE) {
                SaveObjectiveAssignment saveObjectiveAssignment = saveObjectiveAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentId, email))
                returnSavedAssignment.setUserValidity(saveObjectiveAssignment?.getUserValidity())
                returnSavedAssignment.setOptions(assignment?.getOptions())
                returnSavedAssignment.setValidity(assignment?.getValidity())
                if (saveObjectiveAssignment?.getTimespent() != null)
                    returnSavedAssignment.setTimespent(saveObjectiveAssignment.getTimespent())
                return assignment ? new ResponseEntity<>(returnSavedAssignment, HttpStatus.OK) : new ResponseEntity<>("no records found", HttpStatus.NO_CONTENT)
            }
        }
    }

    @ResponseBody
    @PostMapping(value = '/evaluate')
    public ResponseEntity<?> fetchQuestionsAndAnswers(@RequestBody AssignmentSubmissionDetails assignmentSubmissionDetails ){

        AssignmentQuestionsAndAnswers assignmentQuestionsAndAnswers = new AssignmentQuestionsAndAnswers()
        CreateAssignment createAssignment1 = createAssignmentRepository.findByAssignmentid(assignmentSubmissionDetails.assignmentid)
        SubmitAssignment submitAssignment1 =  submitAssignmentRepository.findByTempassignmentid(serviceUtilities.generateFileName(assignmentSubmissionDetails.assignmentid,assignmentSubmissionDetails.email))

        def  user  =  userRepository.findByEmail(assignmentSubmissionDetails.email)
        def  questions = getQuestionsOfStudent(createAssignment1,assignmentSubmissionDetails.email)

        assignmentQuestionsAndAnswers.with {
            createAssignment = createAssignmentConverter.convertToCreateAssignmentResponse(createAssignment1)
            submitAssignment = submitAssignment1
            timespent = formatDuration(submitAssignment1.timespent)
            submittedQuestions = questions
            userName = user ?. firstName ?. capitalize() +' '+user ?. lastName ?. capitalize()
            rollNumber = user.rollNumber
        }
        createAssignment1 && submitAssignment1 ? new ResponseEntity<>(assignmentQuestionsAndAnswers,HttpStatus.OK) : new ResponseEntity<>('Something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)

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

    private Boolean isValidSubmission(CreateAssignment assignment, String email){
        Boolean valid = true
        Date currentDate = new Date()
        if((currentDate-1 > assignment.lastdate && currentDate.date != assignment.lastdate.date) || assignment?.submittedstudents?.contains(email)){
             valid = false
        }
        valid
    }
}
