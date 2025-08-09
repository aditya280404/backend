package com.n8n.clone.node.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.automation.service.SlackService;
import com.n8n.clone.node.NodeExecutionContext;
import com.n8n.clone.node.NodeExecutionResult;
import com.n8n.clone.node.NodeExecutor;

/**
 * A workflow node that posts a message to Slack using the existing
 * {@link SlackService}.  It expects a parameter named "message" which may
 * contain placeholders (e.g. {{response}}) that will be substituted with
 * values from the current input data before posting.  The node does not
 * modify the data flow; it simply returns the existing input data.
 */
@Component
public class SlackNodeExecutor implements NodeExecutor {

    private final SlackService slackService;

    public SlackNodeExecutor(SlackService slackService) {
        this.slackService = slackService;
    }

    @Override
    public String getNodeType() {
        return "slack";
    }

    @Override
    public NodeExecutionResult execute(NodeExecutionContext context) {
        try {
            String messageTemplate = (String) context.getParameter("message");
            if (messageTemplate == null) {
                return NodeExecutionResult.error("Slack node requires a 'message' parameter");
            }
            // Replace placeholders with input data variables
            String processed = messageTemplate;
            Map<String, Object> inputData = context.getInputData();
            if (inputData != null) {
                for (Map.Entry<String, Object> entry : inputData.entrySet()) {
                    String placeholder = "{{" + entry.getKey() + "}}";
                    processed = processed.replace(placeholder, String.valueOf(entry.getValue()));
                }
            }
            slackService.sendNotification(processed);
            // Return existing data unchanged
            return NodeExecutionResult.success(inputData);
        } catch (Exception e) {
            return NodeExecutionResult.error("Slack node failed: " + e.getMessage());
        }
    }
}