package api.submitassignment

import groovy.transform.ToString

/**
 * Created by GnyaniMac on 16/02/18.
 */
@ToString
class SubmitAssignmentResponse {

    AssignmentSubmissionStatus status

    Date submissionDate

    String propicurl

    String email
}
