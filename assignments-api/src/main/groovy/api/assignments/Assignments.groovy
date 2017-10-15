package api.assignments;

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;



/**
 * Created by GnyaniMac on 02/05/17.
 */

@ToString
@EqualsAndHashCode
@TupleConstructor
public class Assignments{

    @JsonProperty
    @NotEmpty
    @NotNull
     String subject;
    @JsonProperty
    @NotEmpty
    @NotNull
     byte[] file;

}
