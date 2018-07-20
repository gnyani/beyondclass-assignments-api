package api.compiler

import api.user.UserDetails
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull
import java.time.LocalDate

/**
 * Created by GnyaniMac on 03/03/18.
 */
@Document(collection = 'savesnippets')
class SaveSnippet {

    @Id
    String snippetid

    @JsonProperty
    @NotNull
    @NotEmpty
    @Indexed
    String email

    UserDetails postedUser

    Date date = new Date()

    @JsonProperty
    @NotEmpty
    @NotNull
    String source

    @JsonProperty
    String description

    @JsonProperty
    @NotEmpty
    @NotNull
    String[] tags

    @JsonProperty
    @NotEmpty
    @NotNull
    String language
}
