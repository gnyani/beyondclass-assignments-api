package api.saveassignment

import constants.AssignmentType

/**
 * Created by GnyaniMac on 14/10/17.
 */
class ReturnSavedAssignment {

    Object[] questions

    Object[] answersContentStates

    String[] answers

    String[] source

    int[] langCodes

    List<String[]> options

    List<int[]> validity

    List<ArrayList> userValidity

    String[] language

    String[] theme

    long timespent

    AssignmentType assignmentType

}
