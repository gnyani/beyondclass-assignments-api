package api.evaluateassignment

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 19/10/17.
 */

@EqualsAndHashCode
@ToString
@TupleConstructor
class AssignmentSubmissionDetails {

    @JsonProperty
    @NotNull
    @NotEmpty
    String assignmentid

    @JsonProperty
    @NotNull
    @NotEmpty
    String email
}
