package api.createassignment

import constants.CodingAssignmentStatus
import groovy.transform.ToString

/**
 * Created by GnyaniMac on 24/12/17.
 */
@ToString
class CodingAssignmentResponse {

    CodingAssignmentStatus codingAssignmentStatus

    String expectedInput

    String expected

    String actual

    String errorMessage

    int failedCase

    int passCount

    int totalCount

    String [] memory

    String [] runtime

}
