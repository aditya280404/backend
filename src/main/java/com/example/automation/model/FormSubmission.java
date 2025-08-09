package com.example.automation.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a form submission.  Each submission captures
 * basic contact details and a message.  The creation time is stored for
 * auditing purposes.
 */
@Document(collection = "form_submissions")
public class FormSubmission {

    @Id
    private String id;
    private String name;
    private String email;
    private String message;
    private Instant createdAt;

    public FormSubmission() {
        this.createdAt = Instant.now();
    }

    public FormSubmission(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}