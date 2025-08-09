package com.n8n.clone.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.n8n.clone.node.NodeExecutionContext;
import com.n8n.clone.node.NodeExecutionResult;
import com.n8n.clone.node.NodeExecutor;

/**
 * Executes user‑defined workflows by chaining node executors.  Each step of
 * the workflow is looked up by its type; the node is supplied with the
 * current working data and parameters and the output is passed to the next
 * step.  If any node fails, execution aborts and an exception is thrown.
 */
@Service
public class WorkflowRuntime {

    private final Map<String, NodeExecutor> executors;

    public WorkflowRuntime(List<NodeExecutor> executorList) {
        // Build a lookup map from node type to executor implementation
        this.executors = executorList.stream()
                .collect(Collectors.toMap(NodeExecutor::getNodeType, e -> e));
    }

    /**
     * Run a workflow with the given initial input.  Each step uses its
     * parameters (if any) and the current data to produce new data.  The
     * final output map is returned.
     *
     * @param workflow definition to execute
     * @param inputData initial input data
     * @return result data from the last node
     * @throws Exception if any node fails or is unknown
     */
    public Map<String, Object> executeWorkflow(WorkflowDefinition workflow, Map<String, Object> inputData) throws Exception {
        Map<String, Object> currentData = new HashMap<>(inputData != null ? inputData : Map.of());
        for (WorkflowStep step : workflow.getSteps()) {
            String type = step.getNodeType();
            NodeExecutor executor = executors.get(type);
            if (executor == null) {
                throw new IllegalStateException("Unknown node type: " + type);
            }
            NodeExecutionContext ctx = new NodeExecutionContext(currentData, step.getParams());
            NodeExecutionResult result = executor.execute(ctx);
            if (!result.isSuccess()) {
                throw new RuntimeException("Node execution failed: " + result.getErrorMessage());
            }
            // merge new output into currentData; new keys override old ones
            if (result.getOutputData() != null) {
                currentData.putAll(result.getOutputData());
            }
        }
        return currentData;
    }
}