package com.engineeringeverything.Assignments.web.Controller

import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GnyaniMac on 27/02/18.
 */
@RestController
class UtilityRestController {

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @ResponseBody
    @GetMapping(value = '/admin/questions/{assignmentid:.+}')
    public ResponseEntity<?> getquestions(@PathVariable(value="assignmentid" , required = true) String assignmentid){

        def assignment = createAssignmentRepository.findByAssignmentid(assignmentid)

        assignment ? new ResponseEntity<>(assignment.questions,HttpStatus.OK) : new ResponseEntity<>("something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
