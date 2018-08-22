package api.saveassignment

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 28/12/17.
 */
@Document(collection = "temp-programming-assignments")
class SaveProgrammingAssignment {

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
    int[] langCodes

    @JsonProperty
    @NotNull
    @NotEmpty
    String[] source

    @JsonProperty
    @NotNull
    @NotEmpty
    String[] theme

    @JsonProperty
    @NotNull
    @NotEmpty
    long timespent

    @JsonProperty
    @NotNull
    @NotEmpty
    String email

}
