package api.createassignment

import com.fasterxml.jackson.annotation.JsonProperty
import constants.AssignmentType
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 25/03/18.
 */

@ToString
@EqualsAndHashCode
class UpdateCreateAssignment {

    @JsonProperty
    @NotNull
    @NotEmpty
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
    List<String []> inputs

    @JsonProperty
    List<String []> outputs

    @JsonProperty
    List<String []> options

    @JsonProperty
    List<int []> validity

    @JsonProperty
    @NotEmpty
    @NotNull
    AssignmentType assignmentType
}
