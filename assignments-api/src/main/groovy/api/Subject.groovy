package api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by GnyaniMac on 06/05/17.
 */
public class Subject {

    @JsonProperty
    public String subject;

    @JsonProperty
    public String teacherclass;

    public Subject(){

    }

    public String getTeacherclass() {
        return teacherclass;
    }

    public void setTeacherclass(String teacherclass) {
        this.teacherclass = teacherclass;
    }

    @JsonCreator
    public Subject(@JsonProperty("subject") String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
