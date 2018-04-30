package api.createassignment

import constants.AssignmentType
import groovy.transform.ToString

/**
 * Created by GnyaniMac on 16/02/18.
 */
@ToString
class CreateAssignmentResponse {

    AssignmentType assignmentType

    List<String []> inputs

    List<String []> outputs

    List<String []> options

    List<int []> validity

    String subject
}
