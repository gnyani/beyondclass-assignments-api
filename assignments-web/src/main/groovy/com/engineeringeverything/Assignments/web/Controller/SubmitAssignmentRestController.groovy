package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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


    @ResponseBody
    @PostMapping(value = '/student/submit')
    public ResponseEntity<?> tempSaveAssignment(@RequestBody SubmitAssignment submitAssignment){
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(submitAssignment.tempassignmentid)
        submitAssignment.setTempassignmentid(submitAssignment.tempassignmentid + submitAssignment.email)
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

}
