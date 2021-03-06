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

    public static String generateFileName(String ... strings) {
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

        userDetails.setCollege(user ?. getCollege())

        userDetails.setUniversity(user ?. getUniversity())

        userDetails.setBranch(user ?. getBranch())

        userDetails
    }

    public static String generateUserName(User user){
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(user.firstName)
        if(user.lastName != null) {
            stringBuilder.append(" ")
            stringBuilder.append(user.lastName)
        }
        stringBuilder.toString()
    }

    public static int ordinalIndexOf(String str, String substr, int n) {
        int pos = str.indexOf(substr)
        while (--n > 0 && pos != -1)
            pos = str.indexOf(substr, pos + 1)
        return pos;
    }

    public String generateUniqueClassIdForTeacher( String batch, String email) {
        def splits = batch.split('-')
        String startyear = splits[0]
        String section = splits[1]
        String endyear = Integer.parseInt(startyear) + 4
        def user = findUserByEmail(email)
        def uniqueClassId = generateFileName(user.university, user.college, user.branch, section, startyear, endyear)
        uniqueClassId
    }
}
