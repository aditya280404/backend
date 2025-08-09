package com.example.automation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.automation.model.FormSubmission;

@Repository
public interface FormSubmissionRepository extends MongoRepository<FormSubmission, String> {
}