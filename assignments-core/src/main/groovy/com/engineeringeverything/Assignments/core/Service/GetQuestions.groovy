package com.engineeringeverything.Assignments.core.Service

import api.createassignment.CreateAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.nio.file.Path

@Service
class GetQuestions {

    @Autowired
    CreateAssignmentRepository  createAssignmentRepository

    @Autowired
    PDFGenerator pdfGenerator



    Path parseQuestionsAndGeneratePDF(String assignmentId){

        List questions = parseQuestions(assignmentId)

        Path pdfPath = generatePDF(assignmentId, questions)

        pdfPath
    }


    public List parseQuestions(String assignmentId){

        CreateAssignment assignment = createAssignmentRepository.findByAssignmentid(assignmentId)

        final List questions = []

        assignment.questions.each {
              StringBuilder stringBuilder = new StringBuilder()
              Object blocks = it.blocks
              blocks.each{
                  stringBuilder.append(it.text).append('\n')
              }
            questions.add(stringBuilder.toString())
        }
        println("Prasing questions from content state")
        questions
    }

    Path generatePDF(String assignmentId, List questions){
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        println("Generating PDF of questions")
        def filePath = pdfGenerator.createPDF(createAssignment.email, createAssignment.subject, questions)
        println("Done")
        filePath
    }
}
