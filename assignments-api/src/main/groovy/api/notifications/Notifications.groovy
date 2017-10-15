package api.notifications

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by GnyaniMac on 17/08/17.
 */

@EqualsAndHashCode
@TupleConstructor
@ToString
@Document (collection = "notifications")
public class Notifications {

    @Id
    String notificationId;

    String content;

    String picurl;

    String notificationType;

    @Indexed
    Date createdAt = new Date();

    @Indexed
    List<NotificationsReadStatus> users;
}
