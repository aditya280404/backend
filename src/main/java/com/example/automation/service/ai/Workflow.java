package com.example.automation.service.ai;

import java.util.List;

/**
 * A simple workflow engine that chains multiple {@link WorkflowNode} instances.
 * The output of each node becomes the input of the next.  The initial input
 * is provided when invoking {@link #run(Object)}.  The final output of the
 * last node is returned.
 */
public class Workflow {

    private final List<WorkflowNode> nodes;

    public Workflow(List<WorkflowNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Run the pipeline with the given initial input.  Each node receives the
     * previous node's output.  If any node throws an exception, execution
     * stops and the exception is propagated.
     *
     * @param input initial input
     * @return final output
     * @throws Exception if any node fails
     */
    public Object run(Object input) throws Exception {
        Object current = input;
        for (WorkflowNode node : nodes) {
            current = node.execute(current);
        }
        return current;
    }
}