package com.engineeringeverything.Assignments.core.Service

import api.notifications.Notifications
import api.notifications.NotificationsReadStatus
import api.user.User
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Repositories.NotificationsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by GnyaniMac on 17/08/17.
 */
@Service
class NotificationService {

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    UserRepository userRepository

    @Autowired
    NotificationsRepository notificationsRepository

    public boolean  storeNotifications(User ActedUser, String content, String type){
        Notifications notifications = new Notifications();
        notifications.setNotificationId(ActedUser.uniqueclassid+'-'+ActedUser.email+'-'+System.currentTimeMillis())
        def users = userRepository.findByUniqueclassid(ActedUser.uniqueclassid)
        users.removeAll(ActedUser)
        Notifications notification = insertNotificationToUsers(users, notifications, content, ActedUser, type)
        notification ? true:false
    }

    public boolean storeNotifications(User ActedUser, String content, String type, String batch){
        Notifications notifications = new Notifications();
        String endyear = batch.substring(0,4).toInteger() + 4
        def notificationId = serviceUtilities.generateFileName(ActedUser.university,ActedUser.college,ActedUser.branch,batch.substring(5),batch.substring(0,4),endyear)
        def users = userRepository.findByUniqueclassid(notificationId)
        String time = System.currentTimeMillis()
        notifications.setNotificationId(serviceUtilities.generateFileName(notificationId,ActedUser.email,time))
        Notifications notification = insertNotificationToUsers(users, notifications, content, ActedUser, type)
        notification ? true:false
    }


    public Notifications insertNotificationToUsers(List<User> users, Notifications notifications, String content, User ActedUser, String type) {
        def usersNotificationsReadStatus =  []
        users.each {
            def userReadStatus = new NotificationsReadStatus()
            userReadStatus.setEmail(it.email)
            userReadStatus.setRead(false)
            usersNotificationsReadStatus.add(userReadStatus)
        }
        notifications.setUsers(usersNotificationsReadStatus)
        notifications.setContent(content)
        notifications.setPicurl(ActedUser.normalpicUrl ?: ActedUser.googlepicUrl)
        notifications.setNotificationType(type)
        def notification = notificationsRepository.save(notifications)
        notification
    }

    public Notifications insertNotificationByEmails(List<String> users, Notifications notifications, String content, User ActedUser, String type) {
        def usersNotificationsReadStatus =  []
        users.each {
            def userReadStatus = new NotificationsReadStatus()
            userReadStatus.setEmail(it)
            userReadStatus.setRead(false)
            usersNotificationsReadStatus.add(userReadStatus)
        }
        notifications.setUsers(usersNotificationsReadStatus)
        notifications.setContent(content)
        notifications.setPicurl(ActedUser.normalpicUrl ?: ActedUser.googlepicUrl)
        notifications.setNotificationType(type)
        def notification = notificationsRepository.save(notifications)
        notification
    }
}
