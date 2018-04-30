package com.engineeringeverything.Assignments.core.Service

import api.evaluateassignment.AssignmentQuestionsAndAnswers
import com.itextpdf.text.Element
import constants.AssignmentType
import org.springframework.stereotype.Service

import com.itextpdf.text.Document
import com.itextpdf.text.DocumentException
import com.itextpdf.text.Font
import com.itextpdf.text.List
import com.itextpdf.text.ListItem
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter

import java.nio.file.Path
import java.nio.file.Paths

@Service
public class PDFGenerator {

    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD)
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD)

    Path createPDF(String author, String subject, java.util.List questions, java.util.List options, java.util.List validity) {
        try {
            String FILE = "/tmp/Questions-${System.currentTimeMillis()}.pdf"
            Document document = new Document()
            PdfWriter.getInstance(document, new FileOutputStream(FILE))
            document.open()
            addMetaData(document, author)
            addTitlePage(document, subject, questions, options, validity)
            document.close()
            return Paths.get(FILE)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    Path createSubmissionPDF(AssignmentQuestionsAndAnswers assignmentQuestionsAndAnswers){
        try{
            String FILE = "/tmp/${assignmentQuestionsAndAnswers.userName}'s-submission-${System.currentTimeMillis()}.pdf"
            Document document = new Document()
            PdfWriter.getInstance(document, new FileOutputStream(FILE))
            document.open()
            addMetaData(document, assignmentQuestionsAndAnswers.userName)
            addSubmissionPage(document, assignmentQuestionsAndAnswers)
            document.close()
            return Paths.get(FILE)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    private static void addMetaData(Document document, String author) {
        document.addTitle("Assignment")
        document.addSubject("Questions List")
        document.addKeywords(" PDF, Beyond Class")
        document.addAuthor("${author}")
        document.addCreator("Beyond Class")
    }

    private static void addSubmissionPage(Document document, AssignmentQuestionsAndAnswers assignmentQuestionsAndAnswers)
            throws DocumentException {
        Paragraph preface = new Paragraph();

        preface.setAlignment(Element.ALIGN_CENTER )
        addEmptyLine(preface, 1)
        if(assignmentQuestionsAndAnswers.createAssignment.subject)
            preface.add(new Paragraph("${assignmentQuestionsAndAnswers.createAssignment.subject} ASSIGNMENT ON BEYOND CLASS", catFont))
        else
            preface.add(new Paragraph("PROGRAMMING ASSIGNMENT ON BEYOND CLASS", catFont))
        addEmptyLine(preface, 1)
        preface.add(new Paragraph("Submitted by ${assignmentQuestionsAndAnswers.userName}(${assignmentQuestionsAndAnswers.rollNumber}) on ${assignmentQuestionsAndAnswers.submitAssignment.submissionDate}", smallBold))
        addEmptyLine(preface, 2)
        List list = new List(true, false, 25)
        if(assignmentQuestionsAndAnswers.createAssignment.assignmentType != AssignmentType.OBJECTIVE){
            assignmentQuestionsAndAnswers.submittedQuestions.eachWithIndex{ question, index ->
                list.add(new ListItem("${question}"))
                List answers =  new List(false, false, 25)
                answers.add(new ListItem("${assignmentQuestionsAndAnswers.submitAssignment.answers[index]}"))
                list.add(answers)
                addEmptyLine(preface, 1)
            }
            preface.add(list)
        }else{
            assignmentQuestionsAndAnswers.submittedQuestions.eachWithIndex { question, index ->
                list.add(new ListItem("${question}"))
                List optionsList =  new List(false, true, 25)
                assignmentQuestionsAndAnswers.createAssignment.options[index].eachWithIndex{ option, index1 ->
                    if(assignmentQuestionsAndAnswers.submitAssignment.userValidity[index].contains(index1))
                        optionsList.add(new ListItem("${option} (Your Answer)"))
                     else
                        optionsList.add(new ListItem("${option}"))
                }
                list.add(optionsList)
            }
            preface.add(list)
            addEmptyLine(preface, 3)
            preface.add(new Paragraph("KEY", smallBold))
            addEmptyLine(preface, 1)
            List key = new List(true, false, 25)
            assignmentQuestionsAndAnswers.createAssignment.validity.each { validity ->
                def convertedList = []
                validity.each{
                    int number = it+65
                    convertedList.add((char)number)
                }
                key.add(new ListItem("${convertedList}", smallBold))
            }
            preface.add(key)
        }

        document.add(preface)
    }


    private static void addTitlePage(Document document, String subject, java.util.List questions, java.util.List options, java.util.List validity)
            throws DocumentException {
        Paragraph preface = new Paragraph();

        preface.setAlignment(Element.ALIGN_CENTER )
        addEmptyLine(preface, 1)
        if(subject)
            preface.add(new Paragraph("${subject} ASSIGNMENT ON BEYOND CLASS", catFont))
        else
            preface.add(new Paragraph("PROGRAMMING ASSIGNMENT ON BEYOND CLASS", catFont))
        addEmptyLine(preface, 1)
        preface.add(new Paragraph("List Of Questions", smallBold))
        addEmptyLine(preface, 3)
        List list = new List(true, false, 30)
        if(options == null || options.size() == 0){
            questions.each{
                list.add(new ListItem("${it}"));
            }
            preface.add(list)
        }else{
            questions.eachWithIndex { question, index ->
               list.add(new ListItem("${question}"))
               List optionsList =  new List(false, true, 25)
               options[index].eachWithIndex{ option, index1 ->
                       optionsList.add(new ListItem("${option}"))
               }
              list.add(optionsList)
            }
            preface.add(list)
            addEmptyLine(preface, 3)
            preface.add(new Paragraph("KEY", smallBold))
            addEmptyLine(preface, 1)
            List key = new List(true, false, 25)
            validity.each{ valid ->
                def convertedList = []
                valid.each{
                    int number = it+65
                    convertedList.add((char)number)
                }
                key.add(new ListItem("${convertedList}", smallBold))
            }
            preface.add(key)
        }

        document.add(preface)
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}