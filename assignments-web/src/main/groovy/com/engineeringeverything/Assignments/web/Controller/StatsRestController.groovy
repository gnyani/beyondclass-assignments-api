package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignmentResponse
import api.submitassignment.SubmitProgrammingAssignmentRequest
import api.stats.StudentSubmissionStats
import api.stats.TeacherAssignmentStats
import api.submitassignment.AssignmentSubmissionStatus
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveProgrammingAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository

import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.web.Converter.SubmitAssignmentConverter
import constants.AssignmentType
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
    SaveProgrammingAssignmentRepository saveProgrammingAssignmentRepository

    @Autowired
    SaveAssignmentRepository saveAssignmentRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    SubmitAssignmentConverter submitAssignmentConverter

    @PostMapping(value = '/teacher/stats')
    public ResponseEntity<?> getStatsForAssignment(@RequestBody String assignmentId){

        TeacherAssignmentStats teacherAssignmentStats = new TeacherAssignmentStats()

        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)

        def submittedAssignments = submitAssignmentRepository.findByTempassignmentidStartingWithOrderByMarksGiven(assignmentId)
        def submitedAssignmentsResponse = []
        submittedAssignments.each {
            submitedAssignmentsResponse.add(submitAssignmentConverter.convertToSubmitAssignmentResponse(it))
        }

        teacherAssignmentStats.setSubmitAssignment(submitedAssignmentsResponse)

        teacherAssignmentStats.setTotalNumberOfDays(createAssignment.lastdate+1 - createAssignment.createDate)
        def numberofDaysLeft = createAssignment.lastdate+1 - (new Date())
        if(numberofDaysLeft>0)
        teacherAssignmentStats.setNumberOfDaysLeft(numberofDaysLeft)
        else
         teacherAssignmentStats.setNumberOfDaysLeft(0)

        teacherAssignmentStats.setNumberOfStudentsSubmitted(submitAssignmentRepository.countByTempassignmentidStartingWith(assignmentId))
        int endindexofUniqueClassId = serviceUtilities.ordinalIndexOf(assignmentId,'-',6)

        teacherAssignmentStats.setTotalEligibleNumberOfStudents(userRepository.countByUniqueclassid(assignmentId.substring(0,endindexofUniqueClassId)))

        teacherAssignmentStats.setPercentdaysCompleted(calculatePerncentage(teacherAssignmentStats.totalNumberOfDays - teacherAssignmentStats.numberOfDaysLeft,teacherAssignmentStats.totalNumberOfDays))
        teacherAssignmentStats.setPercentStudentsSubmitted(calculatePerncentage(teacherAssignmentStats.numberOfStudentsSubmitted,teacherAssignmentStats.totalEligibleNumberOfStudents))

        int assignmentsNotCorrected = submitAssignmentRepository.countByTempassignmentidStartingWithAndStatus(assignmentId,AssignmentSubmissionStatus.PENDING_APPROVAL)
        int assignmentsCorrected = teacherAssignmentStats.numberOfStudentsSubmitted - assignmentsNotCorrected
        teacherAssignmentStats.setEvaluationsDone(assignmentsCorrected)
        teacherAssignmentStats.setPercentOfEvaluationsDone(calculatePerncentage(assignmentsCorrected,teacherAssignmentStats.numberOfStudentsSubmitted))

        if(createAssignment.assignmentType == AssignmentType.THEORY){
            teacherAssignmentStats.studentsWorked = saveAssignmentRepository.countByTempassignmentidStartingWith(assignmentId)
        }else if(createAssignment.assignmentType == AssignmentType.CODING){
            teacherAssignmentStats.studentsWorked = saveProgrammingAssignmentRepository.countByTempassignmentidStartingWith(assignmentId)
        }

        teacherAssignmentStats.setPercentOfStudentsWorked(calculatePerncentage(teacherAssignmentStats.studentsWorked,teacherAssignmentStats.totalEligibleNumberOfStudents))

        new ResponseEntity<>(teacherAssignmentStats,HttpStatus.OK)
    }

    @PostMapping(value = '/student/submission/stats')
    public ResponseEntity<?> getSubmissions(@RequestBody String email){
        StudentSubmissionStats studentSubmissionStats = new StudentSubmissionStats()
        def user = serviceUtilities.findUserByEmail(email)
        studentSubmissionStats.setTotalPoints(user?.points)
        List<SubmitAssignment> myAssignments = submitAssignmentRepository.findByEmailOrderBySubmissionDateDesc(email)
        int acceptedNumber = 0
        int rejectedNumber = 0
        int pendingNumber = 0

        myAssignments.each {
            if(it.status == AssignmentSubmissionStatus.ACCEPTED)
                acceptedNumber++
            else if(it.status == AssignmentSubmissionStatus.REJECTED)
                rejectedNumber++
            else
                pendingNumber++
        }

        studentSubmissionStats.with{
            totalSubmissionsCount = myAssignments.size()
            acceptedCount = acceptedNumber
            rejectedCount = rejectedNumber
            pendingApprovalCount = pendingNumber
            submitAssignmentList = myAssignments
        }
        List<CreateAssignment> assignmentsList = getAssignmentList(myAssignments)
        studentSubmissionStats.setAssignmentsList(assignmentsList)
        new ResponseEntity<>(studentSubmissionStats,HttpStatus.OK)
    }




    public getAssignmentList(List<SubmitAssignment> submitAssignmentList){
        List<CreateAssignment> createAssignmentList = []
        submitAssignmentList.each {
            def id = it.tempassignmentid.replace('-'+it.email,'')
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
