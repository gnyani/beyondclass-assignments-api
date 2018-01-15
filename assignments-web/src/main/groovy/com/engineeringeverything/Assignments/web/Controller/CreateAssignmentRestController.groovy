package com.engineeringeverything.Assignments.web.Controller

import api.createassignment.CreateAssignment
import api.user.User
import com.engineeringeverything.Assignments.core.Repositories.CreateAssignmentRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.EmailUtils
import com.engineeringeverything.Assignments.core.Service.MailService
import com.engineeringeverything.Assignments.core.Service.NotificationService
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.engineeringeverything.Assignments.core.constants.EmailTypes
import constants.AssignmentType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
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
            def message = "You got a new assignment from your teacher ${user.firstName.toUpperCase()}"
            notificationService.storeNotifications(user, message, "teacherstudentspace", createAssignment.batch)
            //sending email to the class
            String uniqueClassId = serviceUtilities.generateFileName(user.university,user.college,user.branch,section,startyear,endyear)
            findUsersAndSendEmail(uniqueClassId,EmailTypes.ASSIGNMENT,user.email)
        }
        assignment ? new ResponseEntity<>("created successfully",HttpStatus.OK) : new ResponseEntity<>("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @GetMapping(value = '/{filename:.+}/delete')
    public ResponseEntity<?> deleteAssignment (@PathVariable(value = "filename",required = true) String filename){
        def deleted = createAssignmentRepository.deleteByAssignmentid(filename)
        deleted ? new ResponseEntity<>('Successful',HttpStatus.OK) : new ResponseEntity<>('something went wrong',HttpStatus.INTERNAL_SERVER_ERROR)
    }

    void findUsersAndSendEmail(String classId,EmailTypes emailTypes,String sender){
        List<User> users = userRepository.findByUniqueclassid(classId)
        def toEmails = []
        users.each {
            toEmails.add(it.email)
        }
        String[] emails = new String[toEmails.size()]
        emails = toEmails.toArray(emails)
        String htmlMessage = emailUtils.createEmailMessage(emailTypes,sender)
        String subject = emailUtils.createSubject(emailTypes)

        mailService.sendHtmlMail(emails,subject,htmlMessage)
    }

}
