package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.insights.Insights
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Service.CsvGenerator
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import constants.AssignmentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import info.debatty.java.stringsimilarity.*

import javax.servlet.http.HttpServletResponse

/**
 * Created by GnyaniMac on 25/11/17.
 */
@RestController
class InsightRestController {

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    CsvGenerator csvGenerator




    @ResponseBody
    @GetMapping(value='/teacher/insights/{submissionid:.+}')
    public ResponseEntity<?> generateInsights(@PathVariable(value="submissionid" , required = true) String submissionid){

        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentid(submissionid)

        def assignmentid= submissionid.replace('-'+submitAssignment ?. email,'')

        def assignment = createAssignmentRepository.findByAssignmentid(assignmentid)

        List<SubmitAssignment> allassignments = submitAssignmentRepository.findByTempassignmentidStartingWithAndSubmissionDateLessThan(assignmentid,submitAssignment ?. submissionDate)

        HashSet<SubmitAssignment> validAssignments = []

        submitAssignment.questionIndex.each{ Object index ->
            allassignments.each {
                if(it.questionIndex.contains(index))
                    validAssignments << it
            }
        }


        return new ResponseEntity<>(computeInsights(submitAssignment,validAssignments),HttpStatus.OK)
    }


    public def computeInsights(SubmitAssignment submitAssignment,HashSet<SubmitAssignment> allassignments){
        Insights insights = submitAssignment.insights
        if(insights == null) {
          insights = new Insights()

          for (def i = 1; i < submitAssignment?.answers?.length + 1; i++) {
              def maxMatch = 0.6
              def matchedUsers = []
              NormalizedLevenshtein l = new NormalizedLevenshtein();
              for (def j = 0; j < allassignments.size(); j++) {

                  def answerIndex = findAnswerToCompare(i - 1, submitAssignment.questionIndex, allassignments[j])
                  if(answerIndex != -1) {
                      def currentMatch = l.distance(submitAssignment?.answers[i - 1], allassignments[j]?.answers[answerIndex])
                      if (1 - currentMatch > maxMatch) {
                          matchedUsers << allassignments[j].email
                      }
                  }
              }
              if (matchedUsers.size() > 0) {
                  if (insights.insight1 == null)
                      insights.insight1 = "Answer ${i} matches more than ${Math.round(maxMatch * 100)}% with answers of ${matchedUsers.toString()}"
                  else if (insights.insight2 == null)
                      insights.insight2 = "Answer ${i} matches more than ${Math.round(maxMatch * 100)}% with answers of ${matchedUsers.toString()}"
                  else if (insights.insight3 == null)
                      insights.insight3 = "Answer ${i} matches more than ${Math.round(maxMatch * 100)}% with answers of ${matchedUsers.toString()}"
                  else if (insights.insight4 == null)
                      insights.insight4 = "Answer ${i} matches more than ${Math.round(maxMatch * 100)}% with answers of ${matchedUsers.toString()}"
                  else if (insights.insight5 == null)
                      insights.insight5 = "Answer ${i} matches more than ${Math.round(maxMatch * 100)}% with answers of ${matchedUsers.toString()}"
                 }
          }
          submitAssignment.insights = insights
          submitAssignmentRepository.save(submitAssignment)
      }
        insights
    }

    int findAnswerToCompare(int index, List questionIndex , SubmitAssignment otherAssignment){

        def questionNumber = questionIndex[index]

        def answerIndex = otherAssignment.questionIndex.indexOf(questionNumber)

        answerIndex
    }


    @PostMapping(value = '/generate/excel/{assignmentid:.+}',produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> generateExcel(@PathVariable(value = "assignmentid" , required = true) String assignmentid,HttpServletResponse response){


        List<SubmitAssignment> submitAssignments = submitAssignmentRepository.findByTempassignmentidStartingWith(assignmentid)
        CreateAssignment assignment = createAssignmentRepository.findByAssignmentid(assignmentid)

        String csv = csvGenerator.toCsv(submitAssignments,assignment)

        byte[] csvBytes = csv.getBytes()

        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-disposition",
                "inline; filename=\"" + assignmentid+'.csv' + "\"");

       new ResponseEntity<>(csvBytes,HttpStatus.OK)
    }
}

