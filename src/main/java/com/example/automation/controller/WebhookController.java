package com.example.automation.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.automation.service.WebhookService;

/**
 * Controller to receive external webhook events.  Currently only GitHub push
 * events are supported.  When triggered a Slack notification is emitted.
 */
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Receive a GitHub webhook payload.  The {@code X-GitHub-Event} header
     * determines the event type.  Only push events are processed by this
     * demo; other events return 200 with no action.
     *
     * @param event    type of GitHub event
     * @param payload  parsed JSON body
     * @return HTTP response
     */
    @PostMapping("/github")
    public ResponseEntity<String> handleGithubWebhook(
            @RequestHeader("X-GitHub-Event") String event,
            @RequestBody Map<String, Object> payload) {
        if ("push".equalsIgnoreCase(event)) {
            webhookService.handleGithubPushEvent(payload);
        }
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }
}