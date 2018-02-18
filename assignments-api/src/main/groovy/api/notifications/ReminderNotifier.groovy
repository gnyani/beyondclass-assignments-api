package api.notifications

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.data.mongodb.core.mapping.Document

import javax.validation.constraints.NotNull

/**
 * Created by GnyaniMac on 18/02/18.
 */
@ToString
@EqualsAndHashCode
@Document(collection = 'reminder-notifier')
class ReminderNotifier {

    @JsonProperty
    @NotNull
    @NotEmpty
    String assignmentId

    @JsonProperty
    @NotNull
    @NotEmpty
    Boolean email

    @JsonProperty
    @NotEmpty
    @NotNull
    Boolean notification

    Date lastNotified = new Date()

}
