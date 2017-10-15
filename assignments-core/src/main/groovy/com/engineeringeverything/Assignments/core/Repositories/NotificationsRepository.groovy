package com.engineeringeverything.Assignments.core.Repositories

import api.notifications.Notifications
import api.notifications.NotificationsReadStatus
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 17/08/17.
 */
public interface NotificationsRepository extends MongoRepository<Notifications,String>{

}