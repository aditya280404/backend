package com.n8n.clone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n8n.clone.workflow.WorkflowDefinition;
import com.n8n.clone.workflow.WorkflowDefinitionRepository;
import com.n8n.clone.workflow.WorkflowRuntime;

/**
 * REST controller exposing CRUD operations for workflow definitions and an
 * endpoint to execute a workflow with arbitrary input data.  This allows
 * clients (including Postman or a frontend application) to create and run
 * workflows dynamically without redeploying the backend.
 */
@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowDefinitionRepository repository;
    private final WorkflowRuntime runtime;

    @Autowired
    public WorkflowController(WorkflowDefinitionRepository repository, WorkflowRuntime runtime) {
        this.repository = repository;
        this.runtime = runtime;
    }

    @PostMapping
    public ResponseEntity<WorkflowDefinition> createWorkflow(@RequestBody WorkflowDefinition workflow) {
        workflow.setId(null); // ensure id is generated
        WorkflowDefinition saved = repository.save(workflow);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<WorkflowDefinition> listWorkflows() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> getWorkflow(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDefinition> updateWorkflow(@PathVariable String id, @RequestBody WorkflowDefinition workflow) {
        return repository.findById(id).map(existing -> {
            workflow.setId(id);
            WorkflowDefinition updated = repository.save(workflow);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        return repository.findById(id).map(existing -> {
            repository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Execute a workflow by id with input data provided in the request body.
     * Returns the final output produced by the last node.
     */
    @PostMapping("/{id}/run")
    public ResponseEntity<?> runWorkflow(@PathVariable String id, @RequestBody(required = false) Map<String, Object> inputData) {
        return repository.findById(id).map(wf -> {
            try {
                Map<String, Object> result = runtime.executeWorkflow(wf, inputData);
                return ResponseEntity.ok(result);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", e.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}