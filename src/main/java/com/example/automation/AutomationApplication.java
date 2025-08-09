package com.example.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the workflow automation backend.  When started the application
 * exposes REST endpoints under /api for triggering various workflows.  You need
 * to configure MongoDB, Redis, email and Slack integration in
 * application.properties before running.
 */
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan({"com.example.automation", "com.n8n.clone"})
@EnableMongoRepositories(basePackages = {"com.example.automation.repository", "com.n8n.clone.workflow"})
public class AutomationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutomationApplication.class, args);
    }
}