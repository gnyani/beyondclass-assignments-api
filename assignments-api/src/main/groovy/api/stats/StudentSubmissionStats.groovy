package api.stats

import api.createassignment.CreateAssignment
import api.submitassignment.SubmitAssignment

/**
 * Created by GnyaniMac on 21/10/17.
 */
class StudentSubmissionStats {

    List<SubmitAssignment> submitAssignmentList

    List<CreateAssignment> assignmentsList

    double totalPoints

    int totalSubmissionsCount

    int pendingApprovalCount

    int acceptedCount

    int rejectedCount
}
