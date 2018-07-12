package com.engineeringeverything.Assignments.web.Controller

import api.compiler.SaveSnippet
import api.user.User
import com.engineeringeverything.Assignments.core.Repositories.SaveSnippetsRepository
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import com.engineeringeverything.Assignments.core.Service.ServiceUtilities
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created by GnyaniMac on 03/03/18.
 */

@RestController
class SaveSnippetController {

    @Autowired
    SaveSnippetsRepository saveSnippetsRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    ServiceUtilities serviceUtilities

    @ResponseBody
    @PostMapping(value='/codeeditor/save')
    public ResponseEntity<?> saveSnippets(@RequestBody SaveSnippet saveSnippet)
    {
        User user = userRepository.findByEmail(saveSnippet.email)
        saveSnippet.with{
            snippetid = serviceUtilities.generateFileName(user.uniqueclassid,email,Long.toString(System.currentTimeMillis()))
            username = serviceUtilities.generateUserName(user)
        }

        SaveSnippet savedSnippet =  saveSnippetsRepository.save(saveSnippet)
        savedSnippet ? new ResponseEntity<?>('Saved successfully',HttpStatus.CREATED) : new ResponseEntity<?>("Somehing went wrong",HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ResponseBody
    @PostMapping(value='/codeeditor/savedlist')
    public ResponseEntity<?> savedSnippetsList(@RequestBody String email){
        def list = saveSnippetsRepository.findByEmailOrderByDateDesc(email)
        list ? new ResponseEntity<?>(list,HttpStatus.OK) : new ResponseEntity<?>('no record founds',HttpStatus.NO_CONTENT)
    }


    @ResponseBody
    @PostMapping(value='/codeeditor/snippet/delete')
    public ResponseEntity<?> deleteSnippet(@RequestBody String snippetid){
        def record = saveSnippetsRepository.deleteBySnippetid(snippetid)
        record ? new ResponseEntity<?>('deleted',HttpStatus.OK) : new ResponseEntity<?>('no record founds',HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
