package com.n8n.clone.workflow;

import java.util.Map;

/**
 * Represents a single step in a user‑defined workflow.  Each step
 * references a node by its type (matching a {@link com.n8n.clone.node.NodeExecutor})
 * and carries a map of parameters to pass to that node at execution time.
 */
public class WorkflowStep {
    private String nodeType;
    private Map<String, Object> params;

    public WorkflowStep() {
    }

    public WorkflowStep(String nodeType, Map<String, Object> params) {
        this.nodeType = nodeType;
        this.params = params;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}