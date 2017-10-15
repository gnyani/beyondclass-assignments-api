package api.createassignment

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 02/10/17.
 */
@EqualsAndHashCode
@TupleConstructor
@ToString
class TeacherAssignmentList {

    @JsonProperty
    @NotNull
    @NotEmpty
    String email

    @JsonProperty
    @NotNull
    @NotEmpty
    String batch
}
