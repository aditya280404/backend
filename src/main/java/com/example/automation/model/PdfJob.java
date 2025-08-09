package com.example.automation.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document capturing the result of a PDF processing job.  The summary
 * field holds a condensed version of the extracted text.  Creation time
 * records when the file was processed.
 */
@Document(collection = "pdf_jobs")
public class PdfJob {

    @Id
    private String id;
    private String fileName;
    private String summary;
    private Instant createdAt;

    public PdfJob() {
        this.createdAt = Instant.now();
    }

    public PdfJob(String fileName, String summary) {
        this.fileName = fileName;
        this.summary = summary;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}