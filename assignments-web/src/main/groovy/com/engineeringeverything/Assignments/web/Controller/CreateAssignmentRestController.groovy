package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.createassignment.SaveCreateAssignment
import api.createassignment.UpdateCreateAssignment
import api.notifications.Notifications
import api.notifications.ReminderNotifier
import api.submitassignment.SubmitAssignment
import api.user.User
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.ReminderNotifierRepository
import com.engineeringeverything.Assignments.core.Repositories.SaveCreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.SubmitAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.EmailUtils
import com.engineeringeverything.Assignments.core.Service.GetQuestions
import com.engineeringeverything.Assignments.core.Service.MailService
import com.engineeringeverything.Assignments.core.Service.NotificationService
import com.engineeringeverything.Assignments.core.Service.PDFGenerator
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.core.constants.EmailTypes
import com.engineeringeverything.Assignments.web.Converter.ObjectConverter
import constants.AssignmentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestParam

import javax.servlet.http.HttpServletResponse
import java.nio.file.Files
import java.nio.file.Path

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
    GetQuestions getQuestions

    @Autowired
    UserRepository userRepository

    @Autowired
    MailService mailService

    @Autowired
    PDFGenerator pdfGenerator

    @Autowired
    SaveCreateAssignmentRepository saveCreateAssignmentRepository

    @Autowired
    ReminderNotifierRepository reminderNotifierRepository

    @Autowired
    SubmitAssignmentRepository submitAssignmentRepository

    @Autowired
    ObjectConverter createAssignmentConverter

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

        if(createAssignment?.author?.realOwner == null){
            createAssignment.author.realOwner = serviceUtilities.toUserDetails(user)
        }

        if(createAssignment.assignmentType == AssignmentType.THEORY)

            createAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                section,startyear,endyear,createAssignment.email,createAssignment.subject,time))
        else if(createAssignment.assignmentType == AssignmentType.CODING)

            createAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                    section,startyear,endyear,createAssignment.email,time))
        else
            createAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                    section,startyear,endyear,createAssignment.email,createAssignment.subject,time))

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
            if(saveCreateAssignment.assignmentType == AssignmentType.THEORY || saveCreateAssignment.assignmentType == AssignmentType.OBJECTIVE)

                saveCreateAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                        section,startyear,endyear,saveCreateAssignment.email,saveCreateAssignment.subject,time))
            else

                saveCreateAssignment.setAssignmentid(serviceUtilities.generateFileName(user.getUniversity(),user.getCollege(),user.getBranch(),
                        section,startyear,endyear,saveCreateAssignment.email,time))
        else{
            saveCreateAssignment.setAssignmentid(saveCreateAssignment1.assignmentid)
            saveCreateAssignment.author.realOwner = saveCreateAssignment1.author.realOwner
            saveCreateAssignment.author.questionSetReferenceId = saveCreateAssignment1.author.questionSetReferenceId
        }
        SaveCreateAssignment savedAssignment = saveCreateAssignmentRepository.save(saveCreateAssignment)

        savedAssignment ? new ResponseEntity<>("Assignment got saved successfully",HttpStatus.OK) : new ResponseEntity<>("Sorry something is not right",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @GetMapping(value="/get/questions/{assignmentId:.+}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getQuestions(@PathVariable(value="assignmentId" , required = true) String assignmentId,HttpServletResponse response){

        Path questionsPDF = getQuestions.parseQuestionsAndGeneratePDF(assignmentId)

        byte[] file = Files.readAllBytes(questionsPDF)

        response.setContentType("application/pdf;charset=UTF-8");
        response.setHeader("Content-disposition",
                "inline; filename=\"" + assignmentId+'.pdf' + "\"");

        Files.delete(questionsPDF)

        file? new ResponseEntity<>(file,HttpStatus.OK) : new ResponseEntity<>("wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }
    @ResponseBody
    @GetMapping(value = '/teacher/get/{assignmentId:.+}')
    public ResponseEntity<?> fetchSavedAssignment(@PathVariable(value="assignmentId" , required = true) String assignmentId){

        SaveCreateAssignment saveCreateAssignment = saveCreateAssignmentRepository.findByAssignmentid(assignmentId)

        saveCreateAssignment ? new ResponseEntity<>(saveCreateAssignment,HttpStatus.OK) : new ResponseEntity<>("not found",HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @GetMapping(value = '/teacher/get/assignment/{assignmentId:.+}')
    public ResponseEntity<?> fetchAssignment(@PathVariable(value="assignmentId" , required = true) String assignmentId){

        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)

        createAssignment ? new ResponseEntity<>(createAssignment,HttpStatus.OK) : new ResponseEntity<>("not found",HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @GetMapping(value = '/teacher/get/assignment/publish/{assignmentId:.+}')
    public ResponseEntity<?> fetchAssignmentForPublish(@PathVariable(value="assignmentId" , required = true) String assignmentId){

        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)

        createAssignment.postedToNetwork = true

        if(createAssignment ?. author ?.realOwner  == null){

            def user = serviceUtilities.findUserByEmail(createAssignment.email)

            createAssignment ?. author ?.realOwner = serviceUtilities.toUserDetails(user)
        }

        createAssignment = createAssignmentRepository.save(createAssignment)

        createAssignment ? new ResponseEntity<>(createAssignment,HttpStatus.OK) : new ResponseEntity<>("not found",HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @PostMapping(value = 'teacher/update/{assignmentId:.+}')
    public  ResponseEntity<?> updateAssignment(@PathVariable(value="assignmentId", required = true) String assignmentId, @RequestBody UpdateCreateAssignment updatedAssignment){
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        if(updatedAssignment.assignmentType == AssignmentType.THEORY){
            createAssignment.questions = updatedAssignment.questions
            createAssignment.lastdate = updatedAssignment.lastdate
            createAssignment.message = updatedAssignment.message
        }
        else if(updatedAssignment.assignmentType == AssignmentType.CODING){
            createAssignment.questions = updatedAssignment.questions
            createAssignment.lastdate = updatedAssignment.lastdate
            createAssignment.message = updatedAssignment.message
            createAssignment.inputs = updatedAssignment.inputs
            createAssignment.outputs = updatedAssignment.outputs
        }else if(updatedAssignment.assignmentType == AssignmentType.OBJECTIVE){
            createAssignment.questions = updatedAssignment.questions
            createAssignment.lastdate = updatedAssignment.lastdate
            createAssignment.message = updatedAssignment.message
            createAssignment.options = updatedAssignment.options
            createAssignment.validity = updatedAssignment.validity
        }
        def newAssignment = createAssignmentRepository.save(createAssignment)
        newAssignment ? new ResponseEntity<?>("Update success",HttpStatus.OK) : new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value = 'teacher/activate/{assignmentId:.+}')
    public ResponseEntity<?> activateAssignment(@PathVariable(value="assignmentId", required = true) String assignmentId,@RequestBody String email){
        CreateAssignment createAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        createAssignment?.submittedstudents?.remove(email)
        def updatedAssignment = createAssignmentRepository.save(createAssignment)
        SubmitAssignment submitAssignment = submitAssignmentRepository.findByTempassignmentid(ServiceUtilities.generateFileName(assignmentId, email))
        if(submitAssignment.marksGiven != null && submitAssignment.marksGiven > 0){
            User user = userRepository.findByEmail(email)
            user.points = user.points - submitAssignment.marksGiven
            userRepository.save(user)
        }
        updatedAssignment ? new ResponseEntity<?>("Success",HttpStatus.OK) : new ResponseEntity<?>("Not Found",HttpStatus.NO_CONTENT)
    }

    @ResponseBody
    @PostMapping(value = '/teacher/notify')
    public ResponseEntity<?> notifyStudents(@RequestBody ReminderNotifier reminderNotifier){
        Boolean notifynow = false
        def previousRemainder = reminderNotifierRepository.findByAssignmentId(reminderNotifier.assignmentId)
        if(previousRemainder){
            int numberOfDaysSince = new Date() - previousRemainder.lastNotified
            if(numberOfDaysSince >= 1)
                notifynow = true
        }else{
            notifynow = true
        }
        if(notifynow) {
            def submittedUsers = submitAssignmentRepository.findByTempassignmentidStartingWithOrderByRollnumber(reminderNotifier.assignmentId)

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

    @ResponseBody
    @PostMapping(value = '/teacher/duplicate/{assignmentId:.+}',produces = 'application/json')
    ResponseEntity <?> duplicateAssignment(@PathVariable(value="assignmentId", required = true) String assignmentId,@RequestBody String batch, @RequestParam(value = "questionsetid", required = false) String questionSetId){
        def refAssignment = createAssignmentRepository.findByAssignmentid(assignmentId)
        if(questionSetId){
            refAssignment.author.questionSetReferenceId = questionSetId
        }
        def refSavedAssignment = createAssignmentConverter.convertToSaveCreateAssignment(refAssignment)

        def batchSplit =  batch.split('-')
        String startYear = batchSplit[0]
        String endYear = Integer.parseInt(startYear)+4

        String refId = refSavedAssignment.assignmentid
        String olderBatch = refId.substring(ServiceUtilities.ordinalIndexOf(refId,'-',3)+1,ServiceUtilities.ordinalIndexOf(refId,'-',6))
        String newId = refId.replace(olderBatch,ServiceUtilities.generateFileName(batchSplit[1], startYear, endYear))

        String oldTime = refId.substring(refId.lastIndexOf('-')+1)
        String newIdWithTime = newId.replace(oldTime,System.currentTimeMillis().toString())

        println("Old id is ${refId} new id is ${newIdWithTime}")
        refSavedAssignment.assignmentid = newIdWithTime
        refSavedAssignment.batch = batch

        def newAssignment = saveCreateAssignmentRepository.save(refSavedAssignment)
        newAssignment ? new ResponseEntity<?>(newAssignment,HttpStatus.CREATED) : new ResponseEntity<?>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
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
        }.then{Exception exception ->
            if(exception)
                println("encountered an exception while sending the email ${exception}")
            println("Emails sent for assignment ${assignmentId}")
        }
    }

}
