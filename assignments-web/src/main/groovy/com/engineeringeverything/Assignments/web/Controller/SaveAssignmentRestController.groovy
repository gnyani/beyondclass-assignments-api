package com.engineeringeverything.Assignments.web.Controller

import api.saveassignment.SaveAssignment
import api.saveassignment.SaveObjectiveAssignment
import api.saveassignment.SaveProgrammingAssignment
import com.engineeringeverything.Assignments.core.Repositories.SaveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveObjectiveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveProgrammingAssignmentRepository
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
    SaveProgrammingAssignmentRepository saveProgrammingAssignmentRepository

    @Autowired
    SaveObjectiveAssignmentRepository saveObjectiveAssignmentRepository

    @Autowired
    ServiceUtilities serviceUtilities


    @ResponseBody
    @PostMapping(value = '/student/save')
    public ResponseEntity<?> tempSaveAssignment(@RequestBody SaveAssignment saveAssignment){
        saveAssignment.setTempassignmentid(serviceUtilities.generateFileName(saveAssignment.tempassignmentid,saveAssignment.email))
        SaveAssignment saveAssignment1 = saveAssignmentRepository.save(saveAssignment)
        saveAssignment1 ? new ResponseEntity<>("Saved Successfully", HttpStatus.OK) : new ResponseEntity<>("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value = '/student/objectiveSave')
    public ResponseEntity<?> tempSaveAssignment(@RequestBody SaveObjectiveAssignment objectiveAssignment){
        objectiveAssignment.setTempassignmentid(serviceUtilities.generateFileName(objectiveAssignment.tempassignmentid,objectiveAssignment.email))
        SaveObjectiveAssignment objectiveAssignment1 = saveObjectiveAssignmentRepository.save(objectiveAssignment)
        objectiveAssignment1 ? new ResponseEntity<>("Saved Successfully", HttpStatus.OK) : new ResponseEntity<>("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value='/hackerrank/assignment/save')
    public ResponseEntity<?> tempSaveProgrammingAssignment(@RequestBody SaveProgrammingAssignment saveProgrammingAssignment){
        saveProgrammingAssignment.setTempassignmentid(serviceUtilities.generateFileName(saveProgrammingAssignment.tempassignmentid,saveProgrammingAssignment.email))
        SaveProgrammingAssignment saveProgrammingAssignment1 = saveProgrammingAssignmentRepository.save(saveProgrammingAssignment)
        saveProgrammingAssignment1 ? new ResponseEntity<>("Saved successfully",HttpStatus.OK) : new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
