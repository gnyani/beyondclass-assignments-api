package com.engineeringeverything.Assignments.web.Converter

import api.createassignment.CreateAssignment
import api.createassignment.CreateAssignmentResponse
import api.createassignment.ListCreateAssignment
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
            options = createAssignment.options
            validity = createAssignment.validity
        }
        createAssignmentResponse
    }

    ListCreateAssignment convertToListCreateAssignment(CreateAssignment createAssignment){
        ListCreateAssignment listCreateAssignment = new ListCreateAssignment()
        listCreateAssignment.with {
            assignmentid = createAssignment.assignmentid
            assignmentType = createAssignment.assignmentType
            batch = createAssignment.batch
            email = createAssignment.email
            propicurl = createAssignment.propicurl
            createDate = createAssignment.createDate
            subject = createAssignment.subject
            message = createAssignment.message
            lastdate = createAssignment.lastdate
        }
        listCreateAssignment
    }


}
