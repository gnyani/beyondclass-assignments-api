package api.submitassignment

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 13/01/18.
 */
@ToString
class SubmitProgrammingAssignmentRequest {

    @Id
    @JsonProperty
    @NotNull
    @NotEmpty
    String tempassignmentid

    @JsonProperty
    @NotNull
    @NotEmpty
    String[] language

    @JsonProperty
    @NotNull
    @NotEmpty
    Object[] questions

    @JsonProperty
    @NotNull
    @NotEmpty
    int[] langcode

    @JsonProperty
    @NotNull
    @NotEmpty
    String[] source

    @JsonProperty
    @NotNull
    @NotEmpty
    long timespent

    @JsonProperty
    @NotNull
    @NotEmpty
    String email

}

