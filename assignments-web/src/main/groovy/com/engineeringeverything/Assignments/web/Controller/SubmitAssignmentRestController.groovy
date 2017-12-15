package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.submitassignment.AssignmentSubmissionStatus
import api.submitassignment.SubmitAssignment
import api.submitassignment.UpdateAssignmentStatus
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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


    @ResponseBody
    @PostMapping(value = '/student/submit')
    public ResponseEntity<?> tempSaveAssignment(@RequestBody SubmitAssignment submitAssignment){
        def user = serviceUtilities.findUserByEmail(submitAssignment.email)
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(submitAssignment.tempassignmentid)
        submitAssignment.setTempassignmentid(serviceUtilities.generateFileName(submitAssignment.tempassignmentid,submitAssignment.email))
        String propicurl = user.normalpicUrl ?: user.googlepicUrl
        submitAssignment.setPropicurl(propicurl)
        submitAssignment.setStatus(AssignmentSubmissionStatus.PENDING_APPROVAL)
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

    @PostMapping(value = '/update/evaluation/{submissionid:.+}')
    public ResponseEntity<?>  updateSubmissionStatus(@PathVariable(value="submissionid" , required = true) String submissionid,@RequestBody UpdateAssignmentStatus updateAssignmentStatus){


        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentid(submissionid)

        submitAssignment.setMarksGiven(updateAssignmentStatus.marks)
        submitAssignment.setStatus(updateAssignmentStatus.status)
        def user = serviceUtilities.findUserByEmail(submitAssignment.email)
        user.setPoints(user.points + submitAssignment.marksGiven)
        userRepository.save(user)

        def submitAssignment1 = submitAssignmentRepository.save(submitAssignment)

        submitAssignment1 ? new ResponseEntity<>('Success',HttpStatus.OK) : new ResponseEntity<>('Something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)
    }


}
