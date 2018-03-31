package com.engineeringeverything.Assignments.core.Repositories;

import api.saveassignment.SaveAssignment;
import api.saveassignment.SaveObjectiveAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SaveObjectiveAssignmentRepository extends MongoRepository<SaveObjectiveAssignment,String> {
    SaveObjectiveAssignment findByTempassignmentid(String id);
}
