package api.compiler

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor

/**
 * Created by GnyaniMac on 02/11/17.
 */

@ToString
@EqualsAndHashCode
@TupleConstructor
class CompilerResponse {
    String compilemessage;

    String [] stdout;

    String [] stderr;

    String [] time;


}
