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

        List questions = parseQuestionsFromId(assignmentId)

        Path pdfPath = generatePDF(assignmentId, questions)

        pdfPath
    }


    public List parseQuestionsFromId(String assignmentId){

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

    public List parseQuestions(Object[] questions){
        final List parsedQuestions = []
        questions.each {
            StringBuilder stringBuilder = new StringBuilder()
            Object blocks = it.blocks
            blocks.each{
                stringBuilder.append(it.text).append('\n')
            }
            parsedQuestions.add(stringBuilder.toString())
        }
        println("Prasing questions from content state")
        parsedQuestions
    }

    Path generatePDF(String assignmentId, List questions){
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        println("Generating PDF of questions")
        def filePath = pdfGenerator.createPDF(createAssignment.email, createAssignment.subject, questions, createAssignment.options, createAssignment.validity)
        println("Done")
        filePath
    }
}
