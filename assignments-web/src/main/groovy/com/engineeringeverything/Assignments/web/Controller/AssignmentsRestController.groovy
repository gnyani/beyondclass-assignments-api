package com.engineeringeverything.Assignments.web.Controller

import api.Subject
import api.assignments.Assignments
import api.user.User
import com.engineeringeverything.Assignments.core.Service.NotificationService
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import com.mongodb.gridfs.GridFSDBFile
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by Gnyani on 05/05/17.
 */

@RestController
class AssignmentsRestController {


    @Autowired
    ServiceUtilities serviceUtils;

    @Autowired
    @Qualifier("assignments")
    GridFsTemplate gridFsTemplate

    @Value('${assignments.host}')
    private String servicehost;

    @Autowired
    NotificationService notificationService


    @ResponseBody
    @PostMapping(value="/upload")
    public ResponseEntity<?> uploadassignments (@RequestBody Assignments assignments, OAuth2Authentication auth)
    {
        println("logged in user ${auth.toString()}")
        String email = serviceUtils.parseEmail(auth)
        User currentuser = serviceUtils.findUserByEmail(email);
        String time = System.currentTimeMillis()
        String filename=serviceUtils.generateFileName(currentuser.getUniqueclassid(),assignments?.getSubject(),currentuser.getEmail(),time)
        InputStream inputStream = new ByteArrayInputStream(assignments.getFile())
        gridFsTemplate.store(inputStream,filename)

        println("this method got called")

        //storing notification
        def message = "You have a new Assignment on subject ${assignments.subject.toUpperCase()} from your friend ${currentuser.firstName}"
        notificationService.storeNotifications(currentuser,message,"assignments")

         gridFsTemplate.store(inputStream,filename) ? new ResponseEntity<>("File uploaded successfully with filename ${filename}",HttpStatus.CREATED)
                : new ResponseEntity<>("Sorry something went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value="/list",produces = "application/json")
    public String[] retrievedefault (@RequestBody Subject subjects, OAuth2Authentication auth)
    {
        String email = serviceUtils.parseEmail(auth)
        User currentuser = serviceUtils.findUserByEmail(email);
        String filename = serviceUtils.generateFileName(currentuser.getUniqueclassid(),subjects.getSubject())
        Query query = new Query().addCriteria(Criteria.where("filename").regex(filename))

        def list = gridFsTemplate.find(query)
        def filelist = []
        list.each {
            filelist << "http://${servicehost}:8080/assignments/${it.getFilename()}";
        }

        filelist
    }

    @RequestMapping(value = "/test")
    public String test(){
        return "hello world"
    }

    @RequestMapping(value="/{filename:.+}",produces = "application/pdf" )
    public ResponseEntity<?> retrieveAssignment(@PathVariable(value = "filename", required = true) Object filename){

        byte[] file;
        Query query = new Query().addCriteria(Criteria.where("filename").is(filename))
        GridFSDBFile imageForOutput = gridFsTemplate.findOne(query)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageForOutput ?. writeTo(baos);
        file = baos ?. toByteArray();

        new ResponseEntity<> (file,HttpStatus.OK)
    }

    @PostMapping(value="/{filename:.+}/download",produces = "application/octet-stream")
    public ResponseEntity<?> downloadAssignment(@PathVariable(value = "filename", required = true) String filename){

        byte[] file
        def filenameactual = filename.substring(filename.indexOf("/") + 1)
        Query query = new Query().addCriteria(Criteria.where("filename").is(filenameactual))
        GridFSDBFile imageForOutput = gridFsTemplate.findOne(query)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageForOutput ?. writeTo(baos);
        file =baos ?. toByteArray();

        new ResponseEntity<>(file,HttpStatus.OK)
    }


}
