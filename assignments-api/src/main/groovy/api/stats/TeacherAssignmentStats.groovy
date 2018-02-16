package api.stats

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignmentResponse
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Created by GnyaniMac on 16/10/17.
 */

@ToString
@EqualsAndHashCode

class TeacherAssignmentStats {

    CreateAssignment createAssignment;

    List<SubmitAssignmentResponse> submitAssignment;

    int numberOfDaysLeft;

    int totalNumberOfDays;

    int numberOfStudentsSubmitted;

    int totalEligibleNumberOfStudents;

    int percentdaysCompleted;

    int percentStudentsSubmitted;

    int evaluationsDone;

    int percentOfEvaluationsDone;
}
