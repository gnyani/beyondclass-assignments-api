package com.engineeringeverything.Assignments.core.Service;

import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * Service class to send mail via SendGrid. Any method should use
 *   #sendMailSynchronized(email) for sending mails. This method used to enforce rate limiting.
 *
 * @author manoj.jangam
 * @since v1.0
 * */
@Service
public class MailService{

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private SendGrid sendGrid;

    @Value('${email.from-address}')
    private String fromEmailAddress;

    private static final Object mailSendSyncLock = new Object();

    public void sendMail(String[] to, String[] cc, String subject, String body)  throws IOException{

        if(to == null || to.length == 0){
            return;
        }

        try {

            Email email = new Email();
            email.addTo(to);
            if(cc != null){
                email.addCc(cc);
            }
            email.setFrom(fromEmailAddress);
            email.setSubject(subject);
            email.setText(body);
            Response response = sendMailSynchronized(email);

            if(response.getCode() != 200){
                log.error("error sending mail. code: " + response.getCode() +
                        " to " + Arrays.toString(email.getTos()) +
                        " subject " + email.getSubject() +
                        " message " + response.getMessage());
            } else {
                log.debug("mail sent to " + Arrays.toString(email.getTos()));
            }

        } catch (SendGridException e) {
            throw new IOException("Unable to send mail.", e);
        }
    }

    public void sendMail(String[] to, String subject, String body) throws IOException{
        sendMail(to, null, subject, body);
    }

    public void sendHtmlMail(String[] to, String subject, String body)  throws IOException{

        if(to == null || to.length == 0){
            return;
        }

        try {

            Email email = new Email();
            email.addTo(to);
            email.setFrom(fromEmailAddress);
            email.setSubject(subject);
            email.setHtml(body);
            Response response = sendMailSynchronized(email);

            if(response.getCode() != 200){
                log.error("error sending mail. code: " + response.getCode() +
                        " to " + Arrays.toString(email.getTos()) +
                        " subject " + email.getSubject() +
                        " message " + response.getMessage());
            } else {
                log.debug("mail sent to " + Arrays.toString(email.getTos()));
            }

        } catch (SendGridException e) {
            throw new IOException("Unable to send mail.", e);
        }
    }

    public void sendMail(String subject, String body, String fileName, String fileType,
                         ByteArrayInputStream file) throws IOException{

        String emailId = fromEmailAddress;

        try {

            Email email = new Email();
            email.addTo(emailId);
            email.setFrom(emailId);
            email.setSubject(subject);
            email.setText(body);
            email.addAttachment(fileName, file);

            Response response = sendMailSynchronized(email);

            if(response.getCode() != 200){
                log.error("error sending mail. code: " + response.getCode() +
                        " to " + Arrays.toString(email.getTos()) +
                        " subject " + email.getSubject() +
                        " message " + response.getMessage());
            } else {
                log.debug("mail sent to " + Arrays.toString(email.getTos()));
            }

        } catch (SendGridException | IOException e) {
            throw new IOException("Unable to send mail.", e);
        }
    }


    private Response sendMailSynchronized(Email email) throws SendGridException{

        Response response;

        synchronized (mailSendSyncLock) {
            response = sendGrid.send(email);
            try{Thread.sleep(1_000);}catch(Exception e){ /* ignore */ }
        }

        return response;
    }

}
