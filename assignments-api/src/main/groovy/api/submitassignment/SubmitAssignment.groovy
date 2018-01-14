package api.submitassignment

import api.createassignment.CodingAssignmentResponse
import api.insights.Insights
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

    CodingAssignmentResponse codingAssignmentResponse

    String mode

    String propicurl;

    double marksGiven;

    Insights insights

    AssignmentSubmissionStatus status;

    Date submissionDate = new Date();

    @Override
    public String toString() {
        return "SubmitAssignment{" +
                "tempassignmentid='" + tempassignmentid + '\'' +
                ", email='" + email + '\'' +
                ", answers=" + Arrays.toString(answers) +
                ", timespent=" + timespent +
                ", propicurl='" + propicurl + '\'' +
                ", marksGiven=" + marksGiven +
                ", insights=" + insights +
                ", status=" + status +
                ", submissionDate=" + submissionDate +
                '}';
    }

    public StringBuilder toCsv(){

        StringBuilder stringBuilder = new StringBuilder()

        if(insights) {
            stringBuilder.append(email).append(',').append(status).append(',').append(submissionDate).append(',').append(timespent)
                    .append(',').append(marksGiven).append(',').append(insights)
        }else{
            stringBuilder.append(email).append(',').append(status).append(',').append(submissionDate).append(',').append(timespent)
                    .append(',').append(marksGiven).append(',').append(codingAssignmentResponse.codingAssignmentStatus).append(',')
            .append(codingAssignmentResponse.totalCount).append(',').append(codingAssignmentResponse.passCount)
        }

        stringBuilder
    }
}
