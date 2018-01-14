package com.engineeringeverything.Assignments.core.Repositories

import api.saveassignment.SaveProgrammingAssignment
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 28/12/17.
 */
interface SaveProgrammingAssignmentRepository extends MongoRepository<SaveProgrammingAssignment,String>{

    SaveProgrammingAssignment findByTempassignmentid(String id)
}
