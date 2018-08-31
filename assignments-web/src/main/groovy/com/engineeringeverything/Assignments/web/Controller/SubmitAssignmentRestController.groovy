package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.insights.Insights
import api.submitassignment.AssignmentSubmissionStatus
import api.submitassignment.SubmitAssignment
import api.submitassignment.UpdateAssignmentStatus
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.EmailUtils
import com.engineeringeverything.Assignments.core.Service.MailService
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.core.constants.EmailTypes
import constants.AssignmentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import static groovyx.gpars.dataflow.Dataflow.task
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GnyaniMac on 14/10/17.
 */
@RestController
class SubmitAssignmentRestController {

    private Logger log = LoggerFactory.getLogger(SubmitAssignmentRestController.class)

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    MailService mailService

    @Autowired
    EmailUtils emailUtils


    @ResponseBody
    @PostMapping(value = '/student/submit')
    public ResponseEntity<?> submitAssignment(@RequestBody SubmitAssignment submitAssignment){
        def user = serviceUtilities.findUserByEmail(submitAssignment.email)
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(submitAssignment.tempassignmentid)

        log.info("<SubmitAssignmentRestController> Submitting students assignment with email ${submitAssignment.email} and to the " +
                "asssignment ${createAssignment.assignmentid} and type is${createAssignment.assignmentType}")

        String profilepicurl = user.normalpicUrl ?: user.googlepicUrl

        submitAssignment.with {
            tempassignmentid = serviceUtilities.generateFileName(submitAssignment.tempassignmentid,submitAssignment.email)
            propicurl = profilepicurl
            username = serviceUtilities.generateUserName(user)
            rollnumber = user ?. rollNumber
            questionIndex = createAssignment.studentQuestionMapping.get(submitAssignment.email)
            status = AssignmentSubmissionStatus.PENDING_APPROVAL
        }

        if(createAssignment.getAssignmentType().equals(AssignmentType.OBJECTIVE)){
            def marks = getMarks(submitAssignment.getUserValidity(), getValidityOfQuestion(createAssignment,submitAssignment.email));
            submitAssignment.setMarksGiven(marks);
            submitAssignment.setStatus(AssignmentSubmissionStatus.ACCEPTED);
            user.setPoints((user.points ? user.points : 0) + marks)
            userRepository.save(user)
            submitAssignment.insights = generateObjectiveInsights(submitAssignment)
        }
        SubmitAssignment submitAssignment1 = submitAssignmentRepository.save(submitAssignment)

        log.info("<SubmitAssignmentRestController> Adding Submitted Student to the list of assignment ${createAssignment.assignmentid}")
        def currentSubmittedStudents =  createAssignment.getSubmittedstudents()
        def submittedDates = createAssignment.getSubmittedDates()
        if(currentSubmittedStudents) {
            currentSubmittedStudents.add(submitAssignment.email)
            submittedDates.put(submitAssignment.email, new Date())
        }
        else
        {
            currentSubmittedStudents = new HashSet<String>()
            currentSubmittedStudents.add(submitAssignment.email)
            submittedDates = new HashMap<String, Date>()
            submittedDates.put(submitAssignment.email,new Date())
        }
        createAssignment.setSubmittedstudents(currentSubmittedStudents)
        createAssignment.setSubmittedDates(submittedDates)
        CreateAssignment createAssignment1 = createAssignmentRepository.save(createAssignment)
        submitAssignment1 && createAssignment1  ? new ResponseEntity<>("Saved Successfully", HttpStatus.OK) : new ResponseEntity<>("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    def Double getMarks(List<int[]> userValidity, List<int[]> validity) {
        int correctCount = 0;
        for(int i=0;i<validity.size();i++){
            if(validity[i]==userValidity[i]){
                correctCount+=1;
            }
        }
        log.info("<SubmitAssignmentRestContoller>Computing marks for objectiveAssignment submission correctcount is ${correctCount} and " +
                "validity is ${validity.size()}")
        return Math.round(correctCount/validity.size()*100)/100* 5;
    }

    Insights generateObjectiveInsights(SubmitAssignment submitAssignment){
        Insights insights = new Insights()
        log.info("<SubmitAssignmentRestContoller> Generating Insights for Objective Assignment")
        def assignmentid= submitAssignment.tempassignmentid.replace('-'+submitAssignment ?. email,'')
        def assignment = createAssignmentRepository.findByAssignmentid(assignmentid)
        def validList = getValidityOfQuestion(assignment,submitAssignment.email)
        def correctCount = getCorrectAnswers(submitAssignment.userValidity,validList)
        insights.insight1 = "Total number of correct answers ${correctCount}/${validList.size()}"

        insights
    }

    int getCorrectAnswers(List<int[]> userValidity, List<int[]> validity) {
        int correctCount = 0;
        for(int i=0;i<validity.size();i++){
            if(validity[i]==userValidity[i]){
                correctCount+=1;
            }
        }
        return correctCount;
    }

    @PostMapping(value = '/update/evaluation/{submissionid:.+}')
    public ResponseEntity<?>  updateSubmissionStatus(@PathVariable(value="submissionid" , required = true) String submissionid,@RequestBody UpdateAssignmentStatus updateAssignmentStatus){

        log.info("<SubmitAssignmentRestContoller>Evaluating the submission with Id ${submissionid}")

        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentid(submissionid)

        String[] idsplit = submissionid.tokenize('-')

        String teacheremail = idsplit[6]

        String teachercollege = idsplit[1]
        Boolean sendEmail = true

        def user = serviceUtilities.findUserByEmail(submitAssignment.email)
        if(submitAssignment.status == AssignmentSubmissionStatus.ACCEPTED )
        {   sendEmail = false
            if(updateAssignmentStatus.status == AssignmentSubmissionStatus.REJECTED)
            {
                user.setPoints(user.points - submitAssignment.marksGiven)
                userRepository.save(user)
            }else{
                if(user.points > 0)
                user.setPoints(user.points - submitAssignment.marksGiven+updateAssignmentStatus.marks)
                else
                    user.setPoints(user.points+updateAssignmentStatus.marks)
                userRepository.save(user)
            }
        }else {
            if(submitAssignment.status == AssignmentSubmissionStatus.REJECTED)
                sendEmail = false
            if(updateAssignmentStatus.status == AssignmentSubmissionStatus.ACCEPTED) {
                user.setPoints(user.points + updateAssignmentStatus.marks)
                userRepository.save(user)
            }
        }
        submitAssignment.setMarksGiven(updateAssignmentStatus.marks)
        submitAssignment.setStatus(updateAssignmentStatus.status)
        submitAssignment.setRemarks(updateAssignmentStatus.remarks)

        def submitAssignment1 = submitAssignmentRepository.save(submitAssignment)
        if(submitAssignment1){
           if(sendEmail) {
               task {
                   String[] emails = [user.email]
                   String htmlMessage = emailUtils.createEmailMessage(EmailTypes.EVALUATION_DONE, teacheremail, teachercollege)
                   String subject = emailUtils.createSubject(EmailTypes.EVALUATION_DONE)
                   mailService.sendHtmlMail(emails, subject, htmlMessage)
               }.then { log.info("<SubmitAssignmentRestController>Sending mail task done to user ${user.email} for Id ${submitAssignment.tempassignmentid}") }
           }else{
               log.info("<SubmitAssignmentRestController> not required to send email")
           }
        }
        submitAssignment1 ? new ResponseEntity<>('Success',HttpStatus.OK) : new ResponseEntity<>('Something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)
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
}

