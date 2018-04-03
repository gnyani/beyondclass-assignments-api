package com.engineeringeverything.Assignments.core.Service

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import constants.AssignmentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by GnyaniMac on 09/01/18.
 */
@Service
class CsvGenerator {

    @Autowired
    UserRepository userRepository

    public String toCsv(List<SubmitAssignment> list,CreateAssignment createAssignment){


        StringBuilder stringBuilder = new StringBuilder()

        if(createAssignment.assignmentType == AssignmentType.THEORY) {
            stringBuilder.append("Roll Number,Email,Status,SubmissionDate,TimeSpent,Marks Given,Insight1,Insight2,Insight3,Insight4,Insight5").append("\n")
        }else if(createAssignment.assignmentType == AssignmentType.CODING){
            stringBuilder.append("Roll Number,Email,Status,SubmissionDate,TimeSpent,Marks Given,Insight1,Insight2,Insight3,Insight4,Insight5,AssignmentStatus,TotalTestCasesCount,TotalPassedCount").append("\n")
        }else if(createAssignment.assignmentType == AssignmentType.OBJECTIVE){
            stringBuilder.append("Roll Number,Email,Status,SubmissionDate,TimeSpent,Marks Given,Insight1").append("\n")
        }

        list ?. each {
            if(it.rollnumber != null)
                stringBuilder.append(it.rollnumber)
            else{
                def user = userRepository.findByEmail(it.email)
                stringBuilder.append(user.rollNumber)
            }
            stringBuilder.append(',').append(it.toCsv()).append('\n')
        }

        stringBuilder.toString()
    }
}
