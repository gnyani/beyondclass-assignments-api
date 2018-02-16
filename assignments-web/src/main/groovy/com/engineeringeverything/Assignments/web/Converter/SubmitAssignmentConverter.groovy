package com.engineeringeverything.Assignments.web.Converter

import api.submitassignment.SubmitAssignment
import api.submitassignment.SubmitAssignmentResponse
import org.springframework.stereotype.Component

/**
 * Created by GnyaniMac on 16/02/18.
 */

@Component
class SubmitAssignmentConverter {

    SubmitAssignmentResponse response = new SubmitAssignmentResponse()

    SubmitAssignmentResponse convertToSubmitAssignmentResponse(SubmitAssignment submitAssignment){

        response.email = submitAssignment.email
        response.propicurl = submitAssignment.propicurl
        response.submissionDate = submitAssignment.submissionDate
        response.status = submitAssignment.status
        response
    }
}
