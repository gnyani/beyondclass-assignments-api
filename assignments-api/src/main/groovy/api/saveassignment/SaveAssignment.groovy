package api.saveassignment

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 14/10/17.
 */
@Document(collection = 'temp-assignments')
class SaveAssignment {

    @Id
    @JsonProperty
    @NotEmpty
    @NotNull
    String tempassignmentid;


    @JsonProperty
    @NotNull
    @NotEmpty
    String email;

    @JsonProperty
    @NotEmpty
    @NotNull
    String[] answers;

    @JsonProperty
    @NotEmpty
    @NotNull
    long timespent;
}
