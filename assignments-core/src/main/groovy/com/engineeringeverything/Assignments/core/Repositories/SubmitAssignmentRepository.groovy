package com.engineeringeverything.Assignments.core.Repositories

import api.submitassignment.AssignmentSubmissionStatus
import api.submitassignment.SubmitAssignment
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 15/10/17.
 */
interface SubmitAssignmentRepository extends MongoRepository<SubmitAssignment,String>{

    List<SubmitAssignment> findByTempassignmentidStartingWith(String id)

    SubmitAssignment findByTempassignmentid(String id)

    int countByTempassignmentidStartingWith(String id)

    int  countByTempassignmentidStartingWithAndStatus(String id,AssignmentSubmissionStatus status)

    List<SubmitAssignment> findByEmailOrderBySubmissionDateDesc(String email)

    int countByEmail(String email)

    int countByEmailAndStatus(String email, AssignmentSubmissionStatus status)
}
