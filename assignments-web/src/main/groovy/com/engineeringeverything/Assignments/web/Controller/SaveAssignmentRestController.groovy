package com.engineeringeverything.Assignments.web.Controller

import api.saveassignment.SaveAssignment
import com.engineeringeverything.Assignments.core.Repositories.SaveAssignmentRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
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
class SaveAssignmentRestController {

    @Autowired
    SaveAssignmentRepository saveAssignmentRepository

    @Autowired
    ServiceUtilities serviceUtilities


    @ResponseBody
    @PostMapping(value = '/student/save')
    public ResponseEntity<?> tempSaveAssignment(@RequestBody SaveAssignment saveAssignment){
        saveAssignment.setTempassignmentid(serviceUtilities.generateFileName(saveAssignment.tempassignmentid,saveAssignment.email))
        SaveAssignment saveAssignment1 = saveAssignmentRepository.save(saveAssignment)
        saveAssignment1 ? new ResponseEntity<>("Saved Successfully", HttpStatus.OK) : new ResponseEntity<>("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
