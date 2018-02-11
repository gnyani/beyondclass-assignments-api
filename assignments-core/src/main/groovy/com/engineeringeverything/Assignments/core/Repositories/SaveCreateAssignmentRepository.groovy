package com.engineeringeverything.Assignments.core.Repositories

import api.createassignment.SaveCreateAssignment
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 10/02/18.
 */
interface SaveCreateAssignmentRepository extends MongoRepository<SaveCreateAssignment,String>{

    List<SaveCreateAssignment> findByAssignmentidStartingWithOrderByCreateDateDesc(String id)

    SaveCreateAssignment findByAssignmentidStartingWith(String id)

    SaveCreateAssignment findByAssignmentid(String id)

    List<SaveCreateAssignment> deleteByAssignmentidStartingWith(String id)

}