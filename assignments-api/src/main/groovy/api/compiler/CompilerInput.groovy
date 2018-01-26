package api.compiler

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 29/10/17.
 */

@EqualsAndHashCode
@ToString
class CompilerInput {

    @JsonProperty
    @NotNull
    @NotEmpty
    String source

    @JsonProperty
    @NotNull
    @NotEmpty
    int lang

    @JsonProperty
    String testcases

    @JsonProperty
    @NotEmpty
    @NotNull
    Object question


    @JsonProperty
    String assignmentid

}
