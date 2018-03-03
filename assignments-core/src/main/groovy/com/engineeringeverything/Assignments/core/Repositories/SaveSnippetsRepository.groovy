package com.engineeringeverything.Assignments.core.Repositories

import api.compiler.SaveSnippet
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Created by GnyaniMac on 03/03/18.
 */
interface SaveSnippetsRepository extends MongoRepository<SaveSnippet, String> {

    List<SaveSnippet> findByEmail(String email)

    Long deleteBySnippetid(String id)
}
