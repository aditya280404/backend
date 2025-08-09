package com.example.automation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.automation.model.PdfJob;

@Repository
public interface PdfJobRepository extends MongoRepository<PdfJob, String> {
}