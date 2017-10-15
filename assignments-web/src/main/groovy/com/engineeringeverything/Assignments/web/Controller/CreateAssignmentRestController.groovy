package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import java.time.LocalDate

/**
 * Created by GnyaniMac on 02/10/17.
 */
@RestController
class CreateAssignmentRestController {

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @ResponseBody
    @PostMapping(value = '/create')
    public ResponseEntity<?> createAssignment( @RequestBody CreateAssignment createAssignment){
        def user = serviceUtilities.findUserByEmail(createAssignment.email)
        def splits = createAssignment.batch.split('-')
        String startyear = splits[0]
        String section = splits[1]
        String endyear = Integer.parseInt(startyear)+ 4
        String propicurl = user.normalpicUrl ?: user.googlepicUrl
        createAssignment.setPropicurl(propicurl)
        String time = System.currentTimeMillis()
        createAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                startyear,endyear,section,createAssignment.email,createAssignment.subject,time))
        def assignment = createAssignmentRepository.save(createAssignment)
        assignment ? new ResponseEntity<>("created successfully",HttpStatus.OK) : new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @GetMapping(value = '/{filename:.+}/delete')
    public ResponseEntity<?> deleteAssignment (@PathVariable(value = "filename",required = true) String filename){
        def deleted = createAssignmentRepository.deleteByAssignmentid(filename)
        deleted ? new ResponseEntity<>('Successful',HttpStatus.OK) : new ResponseEntity<>('something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
