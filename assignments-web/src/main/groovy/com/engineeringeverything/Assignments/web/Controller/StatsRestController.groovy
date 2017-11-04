package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.stats.StudentSubmissionStats
import api.stats.TeacherAssignmentStats
import api.submitassignment.AssignmentSubmissionStatus
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GnyaniMac on 17/10/17.
 */
@RestController
class StatsRestController {

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @PostMapping(value = '/teacher/stats')
    public ResponseEntity<?> getStatsForAssignment(@RequestBody String assignmentId){

        TeacherAssignmentStats teacherAssignmentStats = new TeacherAssignmentStats()

        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        teacherAssignmentStats.setCreateAssignment(createAssignment)

        def submitAssignment = submitAssignmentRepository.findByTempassignmentidStartingWith(assignmentId)
        teacherAssignmentStats.setSubmitAssignment(submitAssignment)

        teacherAssignmentStats.setTotalNumberOfDays(createAssignment.lastdate - createAssignment.createDate)
        def numberofDaysLeft = createAssignment.lastdate - new Date()
        if(numberofDaysLeft>0)
        teacherAssignmentStats.setNumberOfDaysLeft(numberofDaysLeft)
        else
         teacherAssignmentStats.setNumberOfDaysLeft(0)

        teacherAssignmentStats.setNumberOfStudentsSubmitted(submitAssignmentRepository.countByTempassignmentidStartingWith(assignmentId))
        int endindexofUniqueClassId = ordinalIndexOf(assignmentId,'-',6)
        teacherAssignmentStats.setTotalEligibleNumberOfStudents(userRepository.countByUniqueclassid(assignmentId.substring(0,endindexofUniqueClassId)))

        teacherAssignmentStats.setPercentdaysCompleted(calculatePerncentage(teacherAssignmentStats.totalNumberOfDays - teacherAssignmentStats.numberOfDaysLeft,teacherAssignmentStats.totalNumberOfDays))
        teacherAssignmentStats.setPercentStudentsSubmitted(calculatePerncentage(teacherAssignmentStats.numberOfStudentsSubmitted,teacherAssignmentStats.totalEligibleNumberOfStudents))

        int assignmentsNotCorrected = submitAssignmentRepository.countByTempassignmentidStartingWithAndStatus(assignmentId,AssignmentSubmissionStatus.PENDING_APPROVAL)
        int assignmentsCorrected = teacherAssignmentStats.numberOfStudentsSubmitted - assignmentsNotCorrected
        teacherAssignmentStats.setEvaluationsDone(assignmentsCorrected)
        teacherAssignmentStats.setPercentOfEvaluationsDone(calculatePerncentage(assignmentsCorrected,teacherAssignmentStats.numberOfStudentsSubmitted))

        new ResponseEntity<>(teacherAssignmentStats,HttpStatus.OK)
    }

    @PostMapping(value = '/student/submission/stats')
    public ResponseEntity<?> getSubmissions(@RequestBody String email){
        StudentSubmissionStats studentSubmissionStats = new StudentSubmissionStats()
        def user = serviceUtilities.findUserByEmail(email)
        studentSubmissionStats.setTotalPoints(user.points)
       def myAssignments = submitAssignmentRepository.findByEmailOrderBySubmissionDateDesc(email)
        studentSubmissionStats.setSubmitAssignmentList(myAssignments)
        List<CreateAssignment> assignmentsList = getAssignmentList(myAssignments)
        studentSubmissionStats.setAssignmentsList(assignmentsList)
        studentSubmissionStats.setTotalSubmissionsCount(submitAssignmentRepository.countByEmail(email))
        studentSubmissionStats.setAcceptedCount(submitAssignmentRepository.countByEmailAndStatus(email,AssignmentSubmissionStatus.ACCEPTED))
        studentSubmissionStats.setRejectedCount(submitAssignmentRepository.countByEmailAndStatus(email,AssignmentSubmissionStatus.REJECTED))
        studentSubmissionStats.setPendingApprovalCount(submitAssignmentRepository.countByEmailAndStatus(email,AssignmentSubmissionStatus.PENDING_APPROVAL))
        new ResponseEntity<>(studentSubmissionStats,HttpStatus.OK)
    }


    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr)
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1)
        return pos;
    }

    public getAssignmentList(List<SubmitAssignment> submitAssignmentList){
        List<CreateAssignment> createAssignmentList = []
        submitAssignmentList.each {
            def id = it.tempassignmentid.replace(it.email,'')
            createAssignmentList.add(createAssignmentRepository.findByAssignmentid(id))
        }
        createAssignmentList
    }

    public static int calculatePerncentage(int actual, int total){
        if(total == 0)
            return 0
        actual/total * 100;
    }
}
