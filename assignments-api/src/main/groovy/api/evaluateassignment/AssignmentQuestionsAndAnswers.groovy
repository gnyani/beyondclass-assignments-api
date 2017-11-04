package api.evaluateassignment

import api.createassignment.CreateAssignment
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

    CreateAssignment createAssignment

    SubmitAssignment submitAssignment

    String timespent
}
