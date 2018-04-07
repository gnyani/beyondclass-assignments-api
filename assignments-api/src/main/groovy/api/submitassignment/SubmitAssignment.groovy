package api.submitassignment

import api.createassignment.CodingAssignmentResponse
import api.insights.Insights
import com.fasterxml.jackson.annotation.JsonProperty
import constants.CodingAssignmentStatus
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull
import java.util.concurrent.TimeUnit

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
    List<ArrayList> userValidity;

    @JsonProperty
    @NotEmpty
    @NotNull
    long timespent

    String username

    String rollnumber

    CodingAssignmentResponse[] codingAssignmentResponse

    List questionIndex

    String[] mode

    String propicurl;

    double marksGiven;

    String remarks

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

        if(codingAssignmentResponse == null && userValidity == null) {
            stringBuilder.append(email).append(',').append(status).append(',').append(submissionDate).append(',').append(formatDuration(timespent)).append(',').append(marksGiven)
                    .append(',').append(insights)
        }else if(codingAssignmentResponse == null && userValidity != null) {
            stringBuilder.append(email).append(',').append(status).append(',').append(submissionDate).append(',').append(formatDuration(timespent)).append(',').append(marksGiven)
                    .append(',').append(insights.insight1)
        }else {
            stringBuilder.append(email).append(',').append(status).append(',').append(submissionDate).append(',').append(formatDuration(timespent))
                    .append(',').append(marksGiven).append(',').append(insights)

            codingAssignmentResponse.eachWithIndex { var , index ->
                if (var.codingAssignmentStatus == CodingAssignmentStatus.TESTS_FAILED) {
                    stringBuilder.append('\n').append(',,,,,,,,,,').append("Question ${index+1}").append(',').append(var.codingAssignmentStatus).append(',')
                            .append(var.totalCount).append(',').append(var.passCount)
                } else {
                    stringBuilder.append('\n').append(',,,,,,,,,,').append("Question ${index+1}").append(',').append(var.codingAssignmentStatus).append(',')
                            .append('NA').append(',').append('NA')
                }
            }
        }

        stringBuilder
    }

    public static String formatDuration(long millis) {
        long second = (long)(millis / 1000) % 60
        long minute = (long)(millis / (1000 * 60)) % 60
        long hour = (long)(millis / (1000 * 60 * 60)) % 24

        String.format("%02d:%02d:%02d", hour, minute, second)
    }
}
