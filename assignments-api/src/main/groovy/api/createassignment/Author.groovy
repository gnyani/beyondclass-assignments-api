package api.createassignment

import api.user.UserDetails
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.mongodb.core.index.Indexed

/**
 * Created by Gnyani on 30/07/18.
 */

@ToString
@EqualsAndHashCode
class Author {

    UserDetails realOwner

    String questionSetReferenceId
}
