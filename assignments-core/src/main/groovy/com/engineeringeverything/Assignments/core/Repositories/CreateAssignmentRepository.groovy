package com.engineeringeverything.Assignments.core.Repositories

import api.createassignment.CreateAssignment
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 02/10/17.
 */
interface CreateAssignmentRepository extends MongoRepository<CreateAssignment,String> {

    List<CreateAssignment> findByAssignmentidStartingWithOrderByCreateDateDesc(String id)

    List<CreateAssignment> findByAssignmentidStartingWithAndSubmittedstudentsNotContainingAndLastdateAfterOrderByLastdate(String id, String email, Date date)

    CreateAssignment findByAssignmentid(String id)
    
    List<CreateAssignment> deleteByAssignmentid(String id)
}
