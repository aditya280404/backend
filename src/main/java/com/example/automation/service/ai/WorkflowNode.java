package com.example.automation.service.ai;

/**
 * A node in a workflow pipeline.  Each node accepts an input object,
 * performs some processing and returns an output.  Nodes can be chained to
 * build complex processing pipelines.
 */
@FunctionalInterface
public interface WorkflowNode {

    /**
     * Process the given input and produce an output.  Implementations may
     * throw checked exceptions which will propagate to the caller.
     *
     * @param input previous node's output or initial input to the pipeline
     * @return processed output
     * @throws Exception if processing fails
     */
    Object execute(Object input) throws Exception;
}