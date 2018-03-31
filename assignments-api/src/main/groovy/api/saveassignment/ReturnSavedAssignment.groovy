package api.saveassignment

import constants.AssignmentType

/**
 * Created by GnyaniMac on 14/10/17.
 */
class ReturnSavedAssignment {

    Object[] questions

    String[] answers

    String[] source

    Object[] options

    Object[] validity

    Object[] userValidity

    String[] language

    String[] theme

    long timespent

    AssignmentType assignmentType

}
