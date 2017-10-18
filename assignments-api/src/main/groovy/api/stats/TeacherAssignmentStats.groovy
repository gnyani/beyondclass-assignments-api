package api.stats

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignment
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Created by GnyaniMac on 16/10/17.
 */

@ToString
@EqualsAndHashCode

class TeacherAssignmentStats {

    CreateAssignment createAssignment;

    SubmitAssignment submitAssignment;

    int numberOfDaysLeft;

    int totalNumberOfDays;

    int numberOfStudentsSubmitted;

    int totalEligibleNumberOfStudents;

    int percentdaysCompleted;

    int percentStudentsSubmitted;
}
