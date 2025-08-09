package com.example.automation.service.ai;

import com.example.automation.service.EmailService;

/**
 * Workflow node that sends the incoming summary via email.  The input must
 * be a {@link String}; the output is the same summary, allowing further
 * processing if required.  The subject line includes the file name when
 * provided.
 */
public class EmailReportNode implements WorkflowNode {
    private final EmailService emailService;
    private final String recipient;
    private final String fileName;

    public EmailReportNode(EmailService emailService, String recipient, String fileName) {
        this.emailService = emailService;
        this.recipient = recipient;
        this.fileName = fileName;
    }

    @Override
    public Object execute(Object input) throws Exception {
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("EmailReportNode requires a String input");
        }
        String summary = (String) input;
        if (recipient != null && !recipient.isBlank()) {
            String subject = "PDF summary for " + fileName;
            emailService.sendEmail(recipient, subject, summary);
        }
        return summary;
    }
}