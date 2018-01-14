package api.createassignment

import constants.CodingAssignmentStatus
import groovy.transform.ToString

/**
 * Created by GnyaniMac on 24/12/17.
 */
@ToString
class CodingAssignmentResponse {

    CodingAssignmentStatus codingAssignmentStatus

    String expected

    String actual

    String errorMessage

    int failedCase

    int passCount

    int totalCount

}
