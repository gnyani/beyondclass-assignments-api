package api.evaluateassignment

import api.createassignment.CreateAssignmentResponse
import api.submitassignment.SubmitAssignment
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor

/**
 * Created by GnyaniMac on 19/10/17.
 */
@ToString
@EqualsAndHashCode
@TupleConstructor
class AssignmentQuestionsAndAnswers {

    CreateAssignmentResponse createAssignment

    SubmitAssignment submitAssignment

    Object[] submittedQuestions

    String userName

    String rollNumber

    String timespent
}
