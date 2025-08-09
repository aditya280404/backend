package com.example.automation.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service for sending notifications to Slack using the Incoming Webhooks API.
 * The webhook URL is configured via the {@code slack.webhook.url} property.
 */
@Service
public class SlackService {

    private final String webhookUrl;
    private final RestTemplate restTemplate;

    public SlackService(@Value("${slack.webhook.url:}") String webhookUrl) {
        this.webhookUrl = webhookUrl;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Post a message to Slack.  The body is sent as JSON to the webhook endpoint.
     *
     * @param text message text
     */
    public void sendNotification(String text) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            // Slack integration disabled.
            return;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> payload = Collections.singletonMap("text", text);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        try {
            restTemplate.postForEntity(webhookUrl, request, String.class);
        } catch (Exception e) {
            // Log exception in real application; suppressed here for brevity
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }
}