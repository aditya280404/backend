package com.example.automation.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.automation.model.PdfJob;
import com.example.automation.repository.PdfJobRepository;

/**
 * Service for processing PDF files: extract text, summarise it and store the
 * result.  After processing an email can be sent via {@link EmailService}.
 */
@Service
public class PdfService {

    private final PdfJobRepository repository;
    private final EmailService emailService;
    private final SlackService slackService;

    @Autowired
    public PdfService(PdfJobRepository repository,
                      EmailService emailService,
                      SlackService slackService) {
        this.repository = repository;
        this.emailService = emailService;
        this.slackService = slackService;
    }

    /**
     * Process a PDF file via an AI workflow pipeline.  The pipeline
     * sequentially extracts text, summarises it and emails the report to the
     * specified recipient.  The final summary is persisted to MongoDB and
     * returned.  A Slack notification is also emitted.
     *
     * @param file    uploaded PDF
     * @param emailTo address to send the summary to (optional)
     * @return summary text
     * @throws IOException if the PDF cannot be read
     */
    public String processPdf(MultipartFile file, String emailTo) throws IOException {
        try {
            // Build AI pipeline: extract text → summarise → email
            var extractionNode = new com.example.automation.service.ai.PdfExtractionNode();
            var summarisationNode = new com.example.automation.service.ai.TextSummarizationNode();
            var emailNode = new com.example.automation.service.ai.EmailReportNode(emailService, emailTo, file.getOriginalFilename());

            var workflow = new com.example.automation.service.ai.Workflow(
                    java.util.List.of(extractionNode, summarisationNode, emailNode));

            // Execute pipeline
            Object result = workflow.run(file);
            String summary = result != null ? result.toString() : "";

            // Persist job
            PdfJob job = new PdfJob(file.getOriginalFilename(), summary);
            repository.save(job);

            // Notify Slack
            slackService.sendNotification(String.format(
                    "Processed PDF %s; summary length: %d characters", file.getOriginalFilename(), summary.length()));

            return summary;
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException("Error processing PDF: " + e.getMessage(), e);
        }
    }
}