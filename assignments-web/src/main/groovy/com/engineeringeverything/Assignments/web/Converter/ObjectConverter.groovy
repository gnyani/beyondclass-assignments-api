package com.engineeringeverything.Assignments.web.Converter

import api.createassignment.CreateAssignment
import api.createassignment.CreateAssignmentResponse
import api.createassignment.ListCreateAssignment
import api.createassignment.SaveCreateAssignment
import api.duplicateassignment.DuplicateAssignmentResponse
import org.springframework.stereotype.Component

/**
 * Created by GnyaniMac on 16/02/18.
 */
@Component
class ObjectConverter {

    CreateAssignmentResponse convertToCreateAssignmentResponse(CreateAssignment createAssignment){
        CreateAssignmentResponse createAssignmentResponse = new CreateAssignmentResponse()
        createAssignmentResponse.with {
            assignmentType = createAssignment.assignmentType
            inputs = createAssignment.inputs
            outputs = createAssignment.outputs
            options = createAssignment.options
            validity = createAssignment.validity
            subject = createAssignment.subject
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
            author = createAssignment.author
        }
        listCreateAssignment
    }

    SaveCreateAssignment convertToSaveCreateAssignment(CreateAssignment createAssignment){
        SaveCreateAssignment saveCreateAssignment = new SaveCreateAssignment()
        saveCreateAssignment.with {
            assignmentid = createAssignment.assignmentid
            assignmentType = createAssignment.assignmentType
            batch = createAssignment.batch
            email = createAssignment.email
            propicurl = createAssignment.propicurl
            lastdate = createAssignment.lastdate
            subject = createAssignment.subject
            message = createAssignment.message
            questions = createAssignment.questions
            numberOfQuesPerStudent = createAssignment.numberOfQuesPerStudent
            inputs = createAssignment.inputs
            outputs = createAssignment.outputs
            options = createAssignment.options
            validity = createAssignment.validity
            author = createAssignment ?. author
        }
        saveCreateAssignment
    }

    DuplicateAssignmentResponse convertToDuplicateAssignmentResponse(SaveCreateAssignment saveCreateAssignment){

        DuplicateAssignmentResponse duplicateAssignmentResponse = new DuplicateAssignmentResponse()

        duplicateAssignmentResponse.with {
            assignmentType = saveCreateAssignment.assignmentType
            assignmentid = saveCreateAssignment.assignmentid
            batch = saveCreateAssignment.batch
        }

        duplicateAssignmentResponse
    }

}
