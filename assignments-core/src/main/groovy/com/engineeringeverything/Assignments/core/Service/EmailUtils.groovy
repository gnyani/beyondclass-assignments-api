package com.engineeringeverything.Assignments.core.Service

import com.engineeringeverything.Assignments.core.constants.EmailTypes
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by GnyaniMac on 15/01/18.
 */
@Component
class EmailUtils {

    @Value('${beyondclass.host}')
    private String hostName

    public String createEmailMessage(EmailTypes type, String sender){

        String message = "";

        if(type == EmailTypes.ANNOUNCEMENT){

            message = "<h3> You got a new ${type.toString()} from your teacher ${sender} </h3>" +
                    "<br />" +
                    "<form action=\"http://${hostName}/#/teacherstudentspace\">\n" +
                    "    <input type=\"submit\" value=\"View ${type}\" />\n" +
                    "</form>" +
                    "<br />" +
                    "<h4>          --Team Beyond Class"

        }else if(type == EmailTypes.ASSIGNMENT){

            message = "<h3> You got a new ${type.toString()} from your teacher ${sender} </h3>" +
                    "<br />" +
                    "<form action=\"http://${hostName}/#/teacherstudentspace\">\n" +
                    "    <input type=\"submit\" value=\"view ${type}\" />\n" +
                    "</form>" +
                    "<br />" +
                    "<h4>          --Team Beyond Class"

        }else if(type == EmailTypes.EVALUATION_DONE){

            message = "<h3> Your assignment has been evaluated by your teacher ${sender} </h3>" +
                    "<br />" +
                    "<form action=\"http://${hostName}/#/submissions\">\n" +
                    "    <input type=\"submit\" value=\"view result\" />\n" +
                    "</form>" +
                    "<br />" +
                    "<h4>          --Team Beyond Class"
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

        return subject
    }
}
