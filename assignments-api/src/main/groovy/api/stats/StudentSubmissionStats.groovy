package api.stats

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitProgrammingAssignmentRequest
import api.submitassignment.SubmitAssignment
import groovy.transform.ToString

/**
 * Created by GnyaniMac on 21/10/17.
 */
@ToString
class StudentSubmissionStats {

    List<SubmitAssignment> submitAssignmentList

    List<CreateAssignment> assignmentsList

    double totalPoints

    int totalSubmissionsCount

    int pendingApprovalCount

    int acceptedCount

    int rejectedCount
}
