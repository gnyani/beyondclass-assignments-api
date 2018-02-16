package com.engineeringeverything.Assignments.web.Converter

import api.createassignment.CreateAssignment
import api.createassignment.CreateAssignmentResponse
import org.springframework.stereotype.Component

/**
 * Created by GnyaniMac on 16/02/18.
 */
@Component
class CreateAssignmentConverter {

    CreateAssignmentResponse convertToCreateAssignmentResponse(CreateAssignment createAssignment){
        CreateAssignmentResponse createAssignmentResponse = new CreateAssignmentResponse()
        createAssignmentResponse.with {
            assignmentType = createAssignment.assignmentType
            inputs = createAssignment.inputs
            outputs = createAssignment.outputs
        }
        createAssignmentResponse
    }
}
