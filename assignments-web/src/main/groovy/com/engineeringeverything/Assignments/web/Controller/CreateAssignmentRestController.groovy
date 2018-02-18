package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.createassignment.SaveCreateAssignment
import api.notifications.Notifications
import api.notifications.ReminderNotifier
import api.user.User
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.ReminderNotifierRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveCreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.EmailUtils
import com.engineeringeverything.Assignments.core.Service.MailService
import com.engineeringeverything.Assignments.core.Service.NotificationService
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.core.constants.EmailTypes
import constants.AssignmentType
import org.springframework.beans.factory.annotation.Autowired

import static groovyx.gpars.dataflow.Dataflow.task
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


/**
 * Created by GnyaniMac on 02/10/17.
 */
@RestController
class CreateAssignmentRestController {

    @Autowired
    ServiceUtilities serviceUtilities

    @Autowired
    CreateAssignmentRepository createAssignmentRepository

    @Autowired
    NotificationService notificationService

    @Autowired
    EmailUtils emailUtils

    @Autowired
    UserRepository userRepository

    @Autowired
    MailService mailService

    @Autowired
    SaveCreateAssignmentRepository saveCreateAssignmentRepository

    @Autowired
    ReminderNotifierRepository reminderNotifierRepository

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

//    @GetMapping("/mailService")
//    public ResponseEntity<?> sendMail(){
//        List<String> mail = new ArrayList<>();
//        mail.add("gnyani007@gmail.com")
//
//        mailService.sendHtmlMail((String[])mail.toArray(),"helloworld","<h1>this is manoj</h1>");
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @ResponseBody
    @PostMapping(value = '/create')
    public ResponseEntity<?> createAssignment( @RequestBody CreateAssignment createAssignment){
        def user = serviceUtilities.findUserByEmail(createAssignment.email)
        def splits = createAssignment.batch.split('-')
        String startyear = splits[0]
        String section = splits[1]
        String endyear = Integer.parseInt(startyear)+ 4
        String propicurl = user ?.normalpicUrl ?: user?.googlepicUrl
        createAssignment.setPropicurl(propicurl)
        String time = System.currentTimeMillis()

        if(createAssignment.assignmentType == AssignmentType.THEORY)

            createAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                section,startyear,endyear,createAssignment.email,createAssignment.subject,time))
        else

            createAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                    section,startyear,endyear,createAssignment.email,time))

        def assignment = createAssignmentRepository.save(createAssignment)
        if(assignment) {
            //deleting drafts
            if(createAssignment.subject != null) {
                String tempid = serviceUtilities.generateFileName(user.getUniversity(), user.getCollege(), user.getBranch(),
                        section, startyear, endyear, createAssignment.email,createAssignment.subject)

                saveCreateAssignmentRepository.deleteByAssignmentidStartingWith(tempid)
            }else{
                String tempid = serviceUtilities.generateFileName(user.getUniversity(), user.getCollege(), user.getBranch(),
                        section, startyear, endyear, createAssignment.email)

                saveCreateAssignmentRepository.deleteByAssignmentidStartingWith(tempid)
            }

            def message = "You got a new assignment from your teacher ${user.firstName.toUpperCase()}"
            notificationService.storeNotifications(user, message, "teacherstudentspace", createAssignment.batch)
            //sending email to the class
            String uniqueClassId = serviceUtilities.generateFileName(user.university,user.college,user.branch,section,startyear,endyear)
            findUsersAndSendEmail(uniqueClassId,EmailTypes.ASSIGNMENT,user.email,createAssignment.assignmentid)
        }
        assignment ? new ResponseEntity<>("created successfully",HttpStatus.OK) : new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @GetMapping(value = '/{filename:.+}/delete')
    public ResponseEntity<?> deleteAssignment (@PathVariable(value = "filename",required = true) String filename){
        def deleted = saveCreateAssignmentRepository.deleteByAssignmentidStartingWith(filename)
        deleted ? new ResponseEntity<>('Successful',HttpStatus.OK) : new ResponseEntity<>('something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value = '/create/save')
    public ResponseEntity<?> saveAssignment (@RequestBody SaveCreateAssignment saveCreateAssignment){
        def user = serviceUtilities.findUserByEmail(saveCreateAssignment.email)
        def splits = saveCreateAssignment.batch.split('-')
        String startyear = splits[0]
        String section = splits[1]
        String endyear = Integer.parseInt(startyear)+ 4
        String propicurl = user ?.normalpicUrl ?: user?.googlepicUrl
        saveCreateAssignment.setPropicurl(propicurl)
        String time = System.currentTimeMillis()

        SaveCreateAssignment saveCreateAssignment1

        if(saveCreateAssignment.subject != null) {
            String tempid = serviceUtilities.generateFileName(user.getUniversity(), user.getCollege(), user.getBranch(),
                    section, startyear, endyear, saveCreateAssignment.email, saveCreateAssignment.subject)

             saveCreateAssignment1 = saveCreateAssignmentRepository.findByAssignmentidStartingWith(tempid)
        }else{
            String tempid = serviceUtilities.generateFileName(user.getUniversity(), user.getCollege(), user.getBranch(),
                    section, startyear, endyear, saveCreateAssignment.email)

            saveCreateAssignment1 = saveCreateAssignmentRepository.findByAssignmentidStartingWith(tempid)
        }

        if(saveCreateAssignment1 == null)
            if(saveCreateAssignment.assignmentType == AssignmentType.THEORY)

                saveCreateAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                        section,startyear,endyear,saveCreateAssignment.email,saveCreateAssignment.subject,time))
            else

                saveCreateAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                        section,startyear,endyear,saveCreateAssignment.email,time))
        else
            saveCreateAssignment.setAssignmentid(saveCreateAssignment1.assignmentid)

        SaveCreateAssignment savedAssignment = saveCreateAssignmentRepository.save(saveCreateAssignment)

        savedAssignment ? new ResponseEntity<>("Assignment got saved successfully",HttpStatus.OK) : new ResponseEntity<>("Sorry something is not right",HttpStatus.INTERNAL_SERVER_ERROR)
    }
    @ResponseBody
    @GetMapping(value = '/teacher/get/{assignmentId:.+}')
    public ResponseEntity<?> fetchSavedAssignment(@PathVariable(value="assignmentId" , required = true) String assignmentId){

        SaveCreateAssignment saveCreateAssignment = saveCreateAssignmentRepository.findByAssignmentid(assignmentId)

        saveCreateAssignment ? new ResponseEntity<>(saveCreateAssignment,HttpStatus.OK) : new ResponseEntity<>("not found",HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @PostMapping(value = '/teacher/notify')
    public ResponseEntity<?> notifyStudents(@RequestBody ReminderNotifier reminderNotifier){
        Boolean notifynow = false
        def previousRemainder = reminderNotifierRepository.findByAssignmentId(reminderNotifier.assignmentId)
        if(previousRemainder){
            int numberOfDaysSince = new Date() - previousRemainder.lastNotified
            if(numberOfDaysSince > 1)
                notifynow = true
        }else{
            notifynow = true
        }
        if(notifynow) {
            def submittedUsers = submitAssignmentRepository.findByTempassignmentidStartingWith(reminderNotifier.assignmentId)

            CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(reminderNotifier.assignmentId)

            def uniqueclassid = reminderNotifier.assignmentId.substring(0, serviceUtilities.ordinalIndexOf(reminderNotifier.assignmentId, '-', 6))

            def allStudents = userRepository.findByUniqueclassid(uniqueclassid)

            def submittedEmails = []
            submittedUsers.each {
                submittedEmails << it.email
            }

            def allStudentsEmails = []
            allStudents.each {
                allStudentsEmails << it.email
            }

            def studentsToNotify = allStudentsEmails - submittedEmails

            def numberofDaysLeft = createAssignment.lastdate - (new Date())

            if (reminderNotifier.email) {
                task {
                    String htmlMessage = emailUtils.createEmailMessage(EmailTypes.REMINDER_NOTIFIER, createAssignment.email, Integer.toString(numberofDaysLeft))
                    String subject = emailUtils.createSubject(EmailTypes.REMINDER_NOTIFIER)
                    mailService.sendHtmlMail(studentsToNotify as String[], subject, htmlMessage)
                }.then {
                    println("Emails sent successfully")
                }
            }

            if (reminderNotifier.notification) {
                String notificationId = serviceUtilities.generateFileName(uniqueclassid, createAssignment.email, Long.toString(System.currentTimeMillis()))
                Notifications notifications = new Notifications()
                notifications.setNotificationId(notificationId)
                def teacher = userRepository.findByEmail(createAssignment.email)
                String message = "${numberofDaysLeft} days left for your assignment. Reminder from your teacher ${teacher.firstName.capitalize()}"
                notificationService.insertNotificationByEmails(studentsToNotify, notifications, message, teacher, "teacherstudentspace")
            }

            def notification = reminderNotifierRepository.save(reminderNotifier)
            notification ? new ResponseEntity<>("Success", HttpStatus.OK) : new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
        }else{
            new ResponseEntity<?>("Not Allowed to Notify now",HttpStatus.NOT_ACCEPTABLE)
        }
    }

    void findUsersAndSendEmail(String classId,EmailTypes emailTypes,String sender,String assignmentId){

        task {
            List<User> users = userRepository.findByUniqueclassid(classId)
            def toEmails = []
            users.each {
                toEmails.add(it.email)
            }
            String[] emails = new String[toEmails.size()]
            emails = toEmails.toArray(emails)
            String htmlMessage = emailUtils.createEmailMessage(emailTypes, sender)
            String subject = emailUtils.createSubject(emailTypes)

            mailService.sendHtmlMail(emails, subject, htmlMessage)
        }.then{println("Emails sent for assignment ${assignmentId}")}

    }

}
