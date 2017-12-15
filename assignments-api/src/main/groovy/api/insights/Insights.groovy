package api.insights

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Created by GnyaniMac on 25/11/17.
 */
@ToString
@EqualsAndHashCode
@Document(collection = 'insights')
class Insights {

    @Indexed(unique = true)
    String submissionid

    String insight1
    String insight2
}
