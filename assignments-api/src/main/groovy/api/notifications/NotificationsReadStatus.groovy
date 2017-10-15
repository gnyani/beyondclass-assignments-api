package api.notifications

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor;

/**
 * Created by Gnyani on 18/08/17.
 */

@EqualsAndHashCode
@ToString
@TupleConstructor
public class NotificationsReadStatus {

     String email;

     boolean read;
}
