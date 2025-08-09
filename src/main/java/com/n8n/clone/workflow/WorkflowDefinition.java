package com.n8n.clone.workflow;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Top‑level representation of a user‑defined workflow.  A workflow has a
 * name and an ordered list of {@link WorkflowStep}s.  Workflows are stored
 * in MongoDB so they can be created, listed and executed at runtime.
 */
@Document("workflows")
public class WorkflowDefinition {
    @Id
    private String id;
    private String name;
    private List<WorkflowStep> steps;

    public WorkflowDefinition() {
    }

    public WorkflowDefinition(String name, List<WorkflowStep> steps) {
        this.name = name;
        this.steps = steps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }
}