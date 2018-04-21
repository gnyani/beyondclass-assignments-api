package com.engineeringeverything.Assignments.core.Service

import com.itextpdf.text.Element
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

    Path createPDF(String author, String subject, java.util.List questions) {
        try {
            String FILE = "/tmp/Questions-${System.currentTimeMillis()}.pdf"
            Document document = new Document()
            PdfWriter.getInstance(document, new FileOutputStream(FILE))
            document.open()
            addMetaData(document, author)
            addTitlePage(document, subject, questions)
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

    private static void addTitlePage(Document document, String subject, java.util.List questions)
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
        List list = new List(true, false, 10)
        questions.each{
            list.add(new ListItem("${it}"));
        }

        preface.add(list)

        document.add(preface);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
}