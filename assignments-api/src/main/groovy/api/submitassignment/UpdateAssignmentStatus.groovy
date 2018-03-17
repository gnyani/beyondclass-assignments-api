package api.submitassignment

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 19/10/17.
 */
class UpdateAssignmentStatus {

    @JsonProperty
    @NotNull
    @NotEmpty
    double  marks;

    @JsonProperty
    @NotNull
    @NotEmpty
    String remarks

    @JsonProperty
    @NotNull
    @NotEmpty
    AssignmentSubmissionStatus status;
}
