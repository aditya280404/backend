package com.example.automation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.automation.model.FormSubmission;
import com.example.automation.service.FormService;

/**
 * REST controller exposing the form submission endpoint.  Incoming
 * submissions are validated, stored and forwarded to email/Slack via
 * {@link FormService}.
 */
@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    @Autowired
    public FormController(FormService formService) {
        this.formService = formService;
    }

    /**
     * Handle a new form submission.
     *
     * @param request validated request body
     * @return saved entity
     */
    @PostMapping
    public ResponseEntity<FormSubmission> submitForm(@Valid @RequestBody FormRequest request) {
        FormSubmission submission = new FormSubmission(request.getName(), request.getEmail(), request.getMessage());
        FormSubmission saved = formService.handleFormSubmission(submission);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Internal DTO for form submissions.  Validation annotations ensure
     * required fields are provided.
     */
    public static class FormRequest {
        @NotBlank
        private String name;
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String message;

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
    }
}