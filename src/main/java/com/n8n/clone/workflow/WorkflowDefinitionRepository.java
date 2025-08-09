package com.n8n.clone.workflow;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for persisting {@link WorkflowDefinition} documents.  This
 * allows workflows to be created, retrieved, updated and deleted via
 * MongoDB.
 */
@Repository
public interface WorkflowDefinitionRepository extends MongoRepository<WorkflowDefinition, String> {
    Optional<WorkflowDefinition> findByName(String name);
}