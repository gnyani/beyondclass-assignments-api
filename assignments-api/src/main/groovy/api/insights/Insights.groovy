package api.insights

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by GnyaniMac on 25/11/17.
 */

@EqualsAndHashCode
class Insights {

    String insight1
    String insight2
    String insight3
    String insight4
    String insight5

    @Override
    public String toString() {
        return "\"${insight1}\"" + ',' + "\"${insight2}\"" + ',' + "\"${insight3}\"" + ',' + "\"${insight4}\"" + ',' + "\"${insight5}\""
    }
}
