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

/**
 * Created by GnyaniMac on 10/02/18.
 */

@EqualsAndHashCode
@ToString
@TupleConstructor
@Document(collection = "temp-createdAssignments")
class SaveCreateAssignment {

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
    Date lastdate

    @JsonProperty
    String  message

    @JsonProperty
    Object[] questions

    @JsonProperty
    AssignmentType assignmentType

    Author author = new Author()

    @JsonProperty
    int numberOfQuesPerStudent

    @JsonProperty
    List<String []> inputs

    @JsonProperty
    List<String []> outputs

    @JsonProperty
    List<String[]> options;

    @JsonProperty
    List<int[]> validity;

    Boolean postedToNetwork;
}
