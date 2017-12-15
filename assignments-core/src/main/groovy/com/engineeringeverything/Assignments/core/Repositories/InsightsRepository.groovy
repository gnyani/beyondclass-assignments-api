package com.engineeringeverything.Assignments.core.Repositories

import api.insights.Insights
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 28/11/17.
 */
interface InsightsRepository extends MongoRepository<Insights,String>{

    Insights findBySubmissionid(String email)
}
