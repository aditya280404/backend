package com.n8n.clone.node;

import java.util.Map;

/**
 * Holds the data and parameters available to a node at runtime.  Input
 * payloads flow through the workflow and can be accessed by nodes via
 * {@link #getInputData()}.  Node‑specific parameters (e.g. model name,
 * temperature) can be provided when constructing the context and retrieved
 * using {@link #getParameter(String)}.
 */
public class NodeExecutionContext {

    private final Map<String, Object> inputData;
    private final Map<String, Object> parameters;

    public NodeExecutionContext(Map<String, Object> inputData, Map<String, Object> parameters) {
        this.inputData = inputData;
        this.parameters = parameters;
    }

    /**
     * Get the workflow input data.  Nodes can read data produced by previous
     * nodes from this map.
     */
    public Map<String, Object> getInputData() {
        return inputData;
    }

    /**
     * Retrieve a parameter value by name.  Returns {@code null} if the
     * parameter is not present or has a {@code null} value.
     *
     * @param name parameter name
     * @return parameter value or null
     */
    public Object getParameter(String name) {
        return parameters != null ? parameters.get(name) : null;
    }
}