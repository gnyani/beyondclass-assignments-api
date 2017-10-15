package com.engineeringeverything.Assignments.core.Repositories

import api.saveassignment.SaveAssignment
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 14/10/17.
 */
interface SaveAssignmentRepository extends MongoRepository<SaveAssignment,String>{

    SaveAssignment findByTempassignmentid(String email)
}
