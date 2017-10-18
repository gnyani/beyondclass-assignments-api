package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.stats.TeacherAssignmentStats
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
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

    @PostMapping(value = '/teacher/stats')
    public ResponseEntity<?> getStatsForAssignment(@RequestBody String assignmentId){

        TeacherAssignmentStats teacherAssignmentStats = new TeacherAssignmentStats()

        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        teacherAssignmentStats.setCreateAssignment(createAssignment)

        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentidStartingWith(assignmentId)
        teacherAssignmentStats.setSubmitAssignment(submitAssignment)

        teacherAssignmentStats.setTotalNumberOfDays(createAssignment.lastdate - createAssignment.createDate)
        teacherAssignmentStats.setNumberOfDaysLeft(createAssignment.lastdate - new Date())

        teacherAssignmentStats.setNumberOfStudentsSubmitted(submitAssignmentRepository.countByTempassignmentidStartingWith(assignmentId))
        int endindexofUniqueClassId = ordinalIndexOf(assignmentId,'-',6)
        teacherAssignmentStats.setTotalEligibleNumberOfStudents(userRepository.countByUniqueclassid(assignmentId.substring(0,endindexofUniqueClassId)))

        teacherAssignmentStats.setPercentdaysCompleted(calculatePerncentage(teacherAssignmentStats.totalNumberOfDays - teacherAssignmentStats.numberOfDaysLeft,teacherAssignmentStats.totalNumberOfDays))
        teacherAssignmentStats.setPercentStudentsSubmitted(calculatePerncentage(teacherAssignmentStats.numberOfStudentsSubmitted,teacherAssignmentStats.totalEligibleNumberOfStudents))

        new ResponseEntity<>(teacherAssignmentStats,HttpStatus.OK)
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1);
        return pos;
    }

    public static int calculatePerncentage(int actual, int total){
        actual/total * 100;
    }
}
