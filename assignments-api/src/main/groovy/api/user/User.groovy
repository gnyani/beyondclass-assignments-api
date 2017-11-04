package api.user

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull
import java.time.LocalDateTime

/**
 * Created by GnyaniMac on 27/04/17.
 */

@EqualsAndHashCode
@ToString
@Document(collection = "users")
public class User {

    @Id
     String email;

    @JsonProperty
    @Indexed(unique = true)
     String mobilenumber;

    @JsonProperty
    @NotNull
    @NotEmpty
    String userrole;


    @NotEmpty(message = "please enter your name")
    @NotNull(message = "please enter your name")
    @JsonProperty
     String firstName;

    @JsonProperty
     String lastName;

    @NotNull
    @NotEmpty(message = "university cannot be null")
    @JsonProperty
     String university;

    @NotNull
    @NotEmpty(message = "college cannot be null")
    @JsonProperty
     String college;

    @NotNull
    @NotEmpty(message = "branch cannot be null")
    @JsonProperty
     String branch;

    @JsonProperty
     String section;


    @JsonProperty
     String startYear;

     String endYear;

    @JsonProperty
     String[] batches;

    @JsonProperty
    double points;

    @JsonProperty
     String hostel;

    @JsonProperty
     Date dob;

    @Indexed
     String uniqueclassid;

     String googlepicUrl;

     String normalpicUrl;

     LocalDateTime registerdate = LocalDateTime.now();

     boolean accountNonExpired;
     boolean accountNonLocked;
     boolean credentialsNonExpired;
     boolean enabled;

     Set<String> roles = new HashSet<String>();


    public void addRole(String role) {
        roles.add(role);
    }

    public Set<String> getRoles() {
        return roles;
    }


}
