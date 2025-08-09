package com.n8n.clone.node;

import com.n8n.clone.node.NodeExecutionResult;

/**
 * Contract for nodes executed within a workflow.  Each node advertises its
 * type via {@link #getNodeType()} and performs its processing in
 * {@link #execute(NodeExecutionContext)}.  Implementations should be
 * stateless; any required configuration can be injected via Spring.
 */
public interface NodeExecutor {

    /**
     * The type identifier for this node.  Workflows can select nodes by
     * type when orchestrating execution.
     */
    String getNodeType();

    /**
     * Execute the node using the provided context.  The context carries
     * input data and node parameters; implementations should return a
     * {@link NodeExecutionResult} indicating success or failure.
     *
     * @param context execution context
     * @return result of execution
     * @throws Exception if an unexpected error occurs
     */
    NodeExecutionResult execute(NodeExecutionContext context) throws Exception;
}