package api.createassignment

import com.fasterxml.jackson.annotation.JsonProperty
import constants.AssignmentType
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Created by GnyaniMac on 02/10/17.
 */

@EqualsAndHashCode
@ToString
@TupleConstructor
@Document(collection = "createdAssignments")
class CreateAssignment {

    @Id
    String assignmentid

    @JsonProperty
    @NotEmpty
    @NotNull
    String email

    String propicurl;

    Date createDate = new Date()

    @JsonProperty
    String subject

    @JsonProperty
    @NotEmpty
    @NotNull
    String batch

    @JsonProperty
    @NotEmpty
    @NotNull
    Date lastdate

    @JsonProperty
    @NotEmpty
    @NotNull
    String  message

    @JsonProperty
    @NotEmpty
    @NotNull
    Object[] questions

    @JsonProperty
    @NotEmpty
    @NotNull
    AssignmentType assignmentType

    @JsonProperty
    @NotEmpty
    @NotNull
    int numberOfQuesPerStudent

    @JsonProperty
    List<String []> inputs

    @JsonProperty
    List<String []> outputs

    HashMap<String,List<Integer>> studentQuestionMapping

    HashSet<String> submittedstudents

    HashMap<String,Date> submittedDates
}
