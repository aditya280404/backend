package com.n8n.clone.node.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.automation.service.EmailService;
import com.n8n.clone.node.NodeExecutionContext;
import com.n8n.clone.node.NodeExecutionResult;
import com.n8n.clone.node.NodeExecutor;

/**
 * Node executor that sends an email via the configured {@link EmailService}.
 * Parameters:
 * <ul>
 *   <li><code>to</code> – recipient address; may contain placeholders like {{recipient}}</li>
 *   <li><code>subject</code> – subject line; may contain placeholders</li>
 *   <li><code>body</code> – message body; may contain placeholders</li>
 * </ul>
 * At runtime, any placeholders surrounded by double braces will be replaced
 * with corresponding values from the input data map.
 */
@Component
public class EmailNodeExecutor implements NodeExecutor {

    private final EmailService emailService;

    public EmailNodeExecutor(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String getNodeType() {
        return "email";
    }

    @Override
    public NodeExecutionResult execute(NodeExecutionContext ctx) {
        try {
            // Read templates from node parameters.  These may include placeholders (e.g. {{name}}).
            String toTpl   = (String) ctx.getParameter("to");
            String subjTpl = (String) ctx.getParameter("subject");
            String bodyTpl = (String) ctx.getParameter("body");
            if (toTpl == null || subjTpl == null || bodyTpl == null) {
                return NodeExecutionResult.error("Email node requires 'to', 'subject' and 'body' parameters");
            }

            // Replace placeholders in all fields using the current input data
            String to      = replacePlaceholders(toTpl, ctx.getInputData());
            String subject = replacePlaceholders(subjTpl, ctx.getInputData());
            String body    = replacePlaceholders(bodyTpl, ctx.getInputData());

            // Send the email via the central EmailService
            emailService.sendEmail(to, subject, body);

            // Return existing input data unchanged (email node does not generate new data)
            return NodeExecutionResult.success(ctx.getInputData());
        } catch (Exception e) {
            return NodeExecutionResult.error("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Replace all placeholders of the form {{key}} in the given template with
     * corresponding values from the provided input data map.  If a key is not
     * present in the map, the placeholder is left unchanged.
     *
     * @param template a string containing zero or more {{key}} placeholders
     * @param inputData a map of input variables to substitute
     * @return the template with placeholders replaced by their values
     */
    private String replacePlaceholders(String template, Map<String, Object> inputData) {
        if (template == null || inputData == null) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }
        return result;
    }
}