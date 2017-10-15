package com.engineeringeverything.Assignments.core.Service

import api.user.User
import api.user.UserDetails
import com.engineeringeverything.Assignments.core.Repositories.UserRepository
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by GnyaniMac on 01/08/17.
 */
@Service
class ServiceUtilities {

    @Autowired
    UserRepository repository;

    UserDetails userDetails = new UserDetails()

    JsonSlurper jsonSlurper = new JsonSlurper()

    public String parseEmail(Object obj)
    {

        def m = JsonOutput.toJson( obj.getUserAuthentication().getDetails())
        def Json = jsonSlurper.parseText(m);
        String email = Json."email"
        email
    }


    public User findUserByEmail(String email){
        repository.findByEmail(email)
    }

    public String generateFileName(String ... strings) {
        StringBuilder filename = new StringBuilder()
        strings.each {
            if(strings.last() == it)
                filename.append(it)
             else {
                filename.append(it)
                filename.append('-')
            }
        }
        filename.toString();
    }

    public UserDetails toUserDetails(User user){

        String mobilenumber = user ?. getMobilenumber()

        String classid = user ?. getUniqueclassid()

        userDetails.setEmail(user.getEmail())

        userDetails.setFirstName(user ?. getFirstName())

        userDetails.setLastName(user ?. getLastName())

        userDetails.setMobilenumber(mobilenumber)

        userDetails.setUserrole(user ?. getUserrole())

        userDetails.setUniqueclassId(classid)

        userDetails.setGooglepicUrl(user ?. getGooglepicUrl())

        userDetails.setNormalpicUrl(user ?. getNormalpicUrl())

        userDetails
    }
}
