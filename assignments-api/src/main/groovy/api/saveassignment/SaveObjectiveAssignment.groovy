package api.saveassignment

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull


/**
 * Created by manoj on 25/03/18.
 */
@Document(collection = 'temp-objective-assignments')
class SaveObjectiveAssignment {
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
    List<int[]> userValidity = new ArrayList<int[]>()

    @JsonProperty
    @NotEmpty
    @NotNull
    long timespent;
}
