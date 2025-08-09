package com.n8n.clone.node;

import java.util.Map;

/**
 * Represents the outcome of executing a workflow node.  On success the
 * {@link #isSuccess()} flag is {@code true} and the {@link #getOutputData()}
 * contains any resulting data.  On error, {@link #isSuccess()} is
 * {@code false} and {@link #getErrorMessage()} gives a diagnostic message.
 */
public class NodeExecutionResult {

    private final boolean success;
    private final String errorMessage;
    private final Map<String, Object> outputData;

    private NodeExecutionResult(boolean success, String errorMessage, Map<String, Object> outputData) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.outputData = outputData;
    }

    /**
     * Create a successful result with the given output data.
     */
    public static NodeExecutionResult success(Map<String, Object> outputData) {
        return new NodeExecutionResult(true, null, outputData);
    }

    /**
     * Create an error result with the given message.
     */
    public static NodeExecutionResult error(String errorMessage) {
        return new NodeExecutionResult(false, errorMessage, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Map<String, Object> getOutputData() {
        return outputData;
    }
}