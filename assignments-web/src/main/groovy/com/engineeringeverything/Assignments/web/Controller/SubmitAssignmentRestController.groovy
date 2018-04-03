package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
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
    public ResponseEntity<?> tempSaveAssignment(@RequestBody SubmitAssignment submitAssignment){
        def user = serviceUtilities.findUserByEmail(submitAssignment.email)
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(submitAssignment.tempassignmentid)
        submitAssignment.setTempassignmentid(serviceUtilities.generateFileName(submitAssignment.tempassignmentid,submitAssignment.email))
        String propicurl = user.normalpicUrl ?: user.googlepicUrl
        submitAssignment.setPropicurl(propicurl)
        submitAssignment.setUsername(serviceUtilities.generateUserName(user))
        submitAssignment.setRollnumber(user ?. rollNumber)
        submitAssignment.setQuestionIndex(createAssignment.studentQuestionMapping.get(submitAssignment.email))
        submitAssignment.setStatus(AssignmentSubmissionStatus.PENDING_APPROVAL)
        if(createAssignment.getAssignmentType().equals(AssignmentType.OBJECTIVE)){
            def marks = getMarks(submitAssignment.getUserValidity(), getValidityOfQuestion(createAssignment,submitAssignment.email));
            submitAssignment.setMarksGiven(marks);
            submitAssignment.setStatus(AssignmentSubmissionStatus.ACCEPTED);
            user.setPoints((user.points ? user.points : 0) + marks)
            userRepository.save(user)
        }
        SubmitAssignment submitAssignment1 = submitAssignmentRepository.save(submitAssignment)
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
        return (correctCount/validity.size())* 5;
    }

    @PostMapping(value = '/update/evaluation/{submissionid:.+}')
    public ResponseEntity<?>  updateSubmissionStatus(@PathVariable(value="submissionid" , required = true) String submissionid,@RequestBody UpdateAssignmentStatus updateAssignmentStatus){


        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentid(submissionid)

        String[] idsplit = submissionid.tokenize('-')

        String teacheremail = idsplit[6]

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
                   String htmlMessage = emailUtils.createEmailMessage(EmailTypes.EVALUATION_DONE, teacheremail)
                   String subject = emailUtils.createSubject(EmailTypes.EVALUATION_DONE)
                   mailService.sendHtmlMail(emails, subject, htmlMessage)
               }.then { println("Sending mail task done to user ${user.email}") }
           }else{
               println("not required to send email")
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

