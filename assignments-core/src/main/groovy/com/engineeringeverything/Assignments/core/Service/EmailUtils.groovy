package com.engineeringeverything.Assignments.core.Service

import com.engineeringeverything.Assignments.core.constants.EmailTypes
import groovy.text.SimpleTemplateEngine
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by GnyaniMac on 15/01/18.
 */
@Component
class EmailUtils {

    @Value('${beyondclass.host}')
    private String hostName

    public String createEmailMessage(EmailTypes type, String ... strings){

        String message = "";

        String sender = strings[0]
        String noOfDays = strings[1]


      //  if(type == EmailTypes.ANNOUNCEMENT){

        //    message = "<h3> You got a new ${type.toString()} from your teacher ${sender} </h3>" +
          //          "<br />" +
            //        "<form action=\"http://${hostName}/#/teacherstudentspace\">\n" +
              //      "    <input type=\"submit\" value=\"View ${type}\" />\n" +
           //         "</form>" +
           //         "<br />" +
           //         "<h4>          --Team Beyond Class"

       // }
        if(type == EmailTypes.ANNOUNCEMENT)
        {
            def emailTemplate = getClass().getResource("/New_Announcement_posted.html")
            Map<String,String> config = new HashMap<>()
            config.put("type",EmailTypes.ANNOUNCEMENT.toString())
            config.put("hostname",hostName)
            config.put("sender",sender)
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(emailTemplate).make(config)
            message = template.toString()

        }



        else if(type == EmailTypes.ASSIGNMENT){

            def emailTemplate = getClass().getResource("/New_Assignment_Posted.html")
            Map<String,String> config = new HashMap<>()
            config.put("type",EmailTypes.ASSIGNMENT.toString())
            config.put("hostname",hostName)
            config.put("sender",sender)
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(emailTemplate).make(config)
            message = template.toString()

        }/*else if(type == EmailTypes.EVALUATION_DONE){

            message = "<h3> Your assignment has been evaluated by your teacher ${sender} </h3>" +
                    "<br />" +
                    "<form action=\"http://${hostName}/#/submissions\">\n" +
                    "    <input type=\"submit\" value=\"view result\" />\n" +
                    "</form>" +
                    "<br />" +
                    "<h4>          --Team Beyond Class"
        }*/
        else if (type == EmailTypes.EVALUATION_DONE)
        {
            def emailTemplate = getClass().getResource("/Assignment_evaluation.html")
            Map<String,String> config = new HashMap<>()
            config.put("hostname",hostName)
            config.put("sender",sender)
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(emailTemplate).make(config)
            message = template.toString()
        }





      /*  else if(type == EmailTypes.REMINDER_NOTIFIER){
            def emailTemplate = getClass().getResource("/New_Assignment_Posted.html")
            Map<String,String> config = new HashMap<>()
            config.put("type",EmailTypes.ASSIGNMENT.toString())
            config.put("hostname",hostName)
            config.put("sender",sender)
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(emailTemplate).make(config)
            message = template.toString()
        }*/

      //  else if(type == EmailTypes.REMINDER_NOTIFIER){
       //     String noOfDays = strings[1]
        //    message = "<h3> ${noOfDays} days left for your assignment.<h3>" +
         //           "<br />" +
          //          "<h3>Reminder sent by your teacher ${sender} </h3>" +
           //         "<br />" +
            //        "<form action=\"http://${hostName}/#/teacherstudentspace\">\n" +
             //       "    <input type=\"submit\" value=\"view assignment\" />\n" +
              //      "</form>" +
               //     "<br />" +
                //    "<h4>      --Team Beyond Class"
       // }
        else if(type == EmailTypes.REMINDER_NOTIFIER)
        {
            def emailTemplate = getClass().getResource("/Assignment_Notification.html")
            Map<String,String> config = new HashMap()
            config.put("noOfDays",noOfDays)
            config.put("hostname",hostName)
            config.put("sender",sender)
            def engine = new SimpleTemplateEngine()
            def template = engine.createTemplate(emailTemplate).make(config)
            message = template.toString()

        }

        return message
    }

    public String createSubject(EmailTypes type){

        String subject = ""
        if(type == EmailTypes.ASSIGNMENT)
            subject = "You got a new Assignment"
        else if(type == EmailTypes.ANNOUNCEMENT)
            subject = "You got a new Announcement"
        else if(type == EmailTypes.EVALUATION_DONE)
            subject = "Your Assignment has been evaluated"
        else if(type == EmailTypes.REMINDER_NOTIFIER)
            subject = "Reminder for your assignment"

        return subject
    }
}
