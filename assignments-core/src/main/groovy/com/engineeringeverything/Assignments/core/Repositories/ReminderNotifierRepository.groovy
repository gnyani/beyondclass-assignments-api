package com.engineeringeverything.Assignments.core.Repositories

import api.notifications.ReminderNotifier
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 18/02/18.
 */
interface ReminderNotifierRepository  extends MongoRepository<ReminderNotifier,String>{

    ReminderNotifier findByAssignmentId(String assignmentid)
}