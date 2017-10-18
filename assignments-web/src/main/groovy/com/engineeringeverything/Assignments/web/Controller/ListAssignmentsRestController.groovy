package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.createassignment.TeacherAssignmentList
import api.saveassignment.ReturnSavedAssignment
import api.saveassignment.SaveAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveAssignmentRepository
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
 * Created by GnyaniMac on 02/10/17.
 */

@RestController
class ListAssignmentsRestController {

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    SaveAssignmentRepository saveAssignmentRepository

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

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

        list ? new ResponseEntity<>(list,HttpStatus.OK): new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/student/list')
    public ResponseEntity<?> listStudentAssignments (@RequestBody String email){

        def user = serviceUtilities.findUserByEmail(email)
        def assignmentid = serviceUtilities.generateFileName(user.university,user.college,user.branch,user.section,user.startYear,user.endYear)
        def list = createAssignmentRepository.findByAssignmentidStartingWithAndSubmittedstudentsNotContainingOrderByLastdate(assignmentid,email)
        list ? new ResponseEntity<>(list,HttpStatus.OK): new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/get/{assignmentId:.+}')
    public ResponseEntity<?> fetchAssignment(@PathVariable(value="assignmentId" , required = true) String assignmentId,@RequestBody String email){
        ReturnSavedAssignment returnSavedAssignment = new ReturnSavedAssignment()
        CreateAssignment assignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        returnSavedAssignment.setQuestions(assignment ?. getQuestions())
        SaveAssignment saveAssignment = saveAssignmentRepository.findByTempassignmentid(assignmentId+email)
        returnSavedAssignment.setAnswers(saveAssignment ?. answers)
        assignment ? new ResponseEntity<>(returnSavedAssignment,HttpStatus.OK) : new ResponseEntity<>("no records found",HttpStatus.NO_CONTENT)
    }
}
