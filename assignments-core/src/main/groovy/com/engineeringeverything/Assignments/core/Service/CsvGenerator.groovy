package com.engineeringeverything.Assignments.core.Service

import api.submitassignment.SubmitAssignment
import org.springframework.stereotype.Service

/**
 * Created by GnyaniMac on 09/01/18.
 */
@Service
class CsvGenerator {

    public String toCsv(List<SubmitAssignment> list){


        StringBuilder stringBuilder = new StringBuilder()

        if(list[0].insights) {
            stringBuilder.append("Email,Status,SubmissionDate,TimeSpent,Marks Given,Insights").append("\n")
        }else{
            stringBuilder.append("Email,Status,SubmissionDate,TimeSpent,Marks Given,AssignmentStatus,TotalTestCasesCount,TotalPassedCount").append("\n")
        }

        list ?. each {
            stringBuilder.append(it.toCsv()).append('\n')
        }

        stringBuilder.toString()
    }
}
