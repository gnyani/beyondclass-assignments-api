package com.engineeringeverything.Assignments.web.Controller

import api.insights.Insights
import api.submitassignment.SubmitAssignment
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.InsightsRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import info.debatty.java.stringsimilarity.*

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
    UserRepository userRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    InsightsRepository insightsRepository

    @ResponseBody
    @GetMapping(value='/teacher/insights/{submissionid:.+}')
    public ResponseEntity<?> generateInsights(@PathVariable(value="submissionid" , required = true) String submissionid){

        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentid(submissionid)

        def assignmentid= submissionid.replace('-'+submitAssignment ?. email,'')

        List<SubmitAssignment> allassignments = submitAssignmentRepository.findByTempassignmentidStartingWithAndSubmissionDateLessThan(assignmentid,submitAssignment ?. submissionDate)

        new ResponseEntity<>(computeInsights(allassignments, submitAssignment),HttpStatus.OK)

    }

   public def computeInsights(List<SubmitAssignment> allassignments, SubmitAssignment submitAssignment ){
       Insights insights
       insights = insightsRepository.findBySubmissionid(submitAssignment.tempassignmentid)
      if(insights == null) {
          insights = new Insights()
          insights.submissionid = submitAssignment.tempassignmentid

          for (def i = 1; i < submitAssignment?.answers?.length + 1; i++) {
              def maxMatch = 0
              def maxIndex = 0
              NormalizedLevenshtein l = new NormalizedLevenshtein();
              for(def j=0; j<allassignments.size();j++)
              {
                  def currentMatch = l.distance(submitAssignment?.answers[i - 1], allassignments[j]?.answers[i - 1])
                  if (1-currentMatch > maxMatch) {
                      maxMatch = 1 - currentMatch
                      maxIndex = j
                  }
              }
              if (maxMatch > 0.4) {
                  if (insights.insight1 == null)
                      insights.insight1 = "Answer ${i} matches ${Math.round(maxMatch * 100)}% with answer of ${allassignments[maxIndex].email}"
                  else if (insights.insight2 == null)
                      insights.insight2 = "Answer ${i} matches ${Math.round(maxMatch * 100)}% with answer of ${allassignments[maxIndex].email}"
              }
          }
          insightsRepository.save(insights)
      }
       insights
   }
}

