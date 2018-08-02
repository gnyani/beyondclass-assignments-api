package api.user

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor;


@EqualsAndHashCode
@ToString
@TupleConstructor
public class UserDetails {

     String email

     String firstName

     String lastName

     String mobilenumber

     String userrole

     String uniqueclassId

     String googlepicUrl

     String normalpicUrl

     String college

     String university

     String branch
}
