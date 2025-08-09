package com.example.automation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.automation.model.FormSubmission;
import com.example.automation.repository.FormSubmissionRepository;

/**
 * Orchestrates the form submission workflow: persist the submission, send an
 * acknowledgement email to the user and notify Slack.  All actions occur
 * synchronously; for heavy workloads you could offload email/Slack to
 * asynchronous queues.
 */
@Service
public class FormService {

    private final FormSubmissionRepository repository;
    private final EmailService emailService;
    private final SlackService slackService;

    @Autowired
    public FormService(FormSubmissionRepository repository,
                       EmailService emailService,
                       SlackService slackService) {
        this.repository = repository;
        this.emailService = emailService;
        this.slackService = slackService;
    }

    /**
     * Process a form submission: save it to MongoDB, email the submitter and
     * notify Slack.  Returns the persisted entity.
     *
     * @param submission incoming submission
     * @return saved document
     */
    public FormSubmission handleFormSubmission(FormSubmission submission) {
        // Persist the submission
        FormSubmission saved = repository.save(submission);

        // Send acknowledgement email
        String subject = "Thanks for contacting us";
        String body = String.format(
                "Hi %s,\n\nThank you for your message: \"%s\". We'll get back to you shortly.\n\nRegards,\nAutomation Team",
                saved.getName(), saved.getMessage());
        emailService.sendEmail(saved.getEmail(), subject, body);

        // Notify Slack channel
        slackService.sendNotification(String.format(
                "New form submission from %s (%s): %s",
                saved.getName(), saved.getEmail(), saved.getMessage()));

        return saved;
    }
}