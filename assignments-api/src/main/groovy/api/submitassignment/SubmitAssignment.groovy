package api.submitassignment

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 15/10/17.
 */
@Document(collection = 'submitted-assignments')
@ToString
@EqualsAndHashCode
class SubmitAssignment {

    @Id
    @JsonProperty
    @NotEmpty
    @NotNull
    String tempassignmentid;


    @JsonProperty
    @NotNull
    @NotEmpty
    @Indexed
    String email;

    @JsonProperty
    @NotEmpty
    @NotNull
    String[] answers;

    @JsonProperty
    @NotEmpty
    @NotNull
    long timespent

    String propicurl;

    double marksGiven;

    AssignmentSubmissionStatus status;

    Date submissionDate = new Date();
}
