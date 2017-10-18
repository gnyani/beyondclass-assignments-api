package com.engineeringeverything.Assignments.core.Repositories

import api.submitassignment.SubmitAssignment
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 15/10/17.
 */
interface SubmitAssignmentRepository extends MongoRepository<SubmitAssignment,String>{

    SubmitAssignment findByTempassignmentidStartingWith(String id)

    int countByTempassignmentidStartingWith(String id)
}
