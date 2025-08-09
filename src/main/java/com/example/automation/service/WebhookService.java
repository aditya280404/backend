package com.example.automation.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.n8n.clone.workflow.WorkflowDefinitionRepository;
import com.n8n.clone.workflow.WorkflowRuntime;
import org.springframework.stereotype.Service;

/**
 * Service for handling external webhook events.  Currently supports GitHub
 * push events and forwards information to Slack.  For production use
 * you should verify signatures and handle different event types.
 */
@Service
public class WebhookService {

    private final WorkflowDefinitionRepository workflowRepo;
    private final WorkflowRuntime workflowRuntime;
    private final SlackService slackFallback; // optional fallback

    public WebhookService(WorkflowDefinitionRepository workflowRepo,
                          WorkflowRuntime workflowRuntime,
                          SlackService slackFallback) {
        this.workflowRepo = workflowRepo;
        this.workflowRuntime = workflowRuntime;
        this.slackFallback = slackFallback;
    }

    @SuppressWarnings("unchecked")
    public void handleGithubPushEvent(Map<String, Object> payload) {
        try {
            Map<String,Object> repo = (Map<String,Object>) payload.get("repository");
            String fullName = repo != null ? String.valueOf(repo.get("full_name")) : "unknown/repo";
            String ref = String.valueOf(payload.get("ref"));
            String branch = (ref != null && ref.contains("/")) ? ref.substring(ref.lastIndexOf('/') + 1) : ref;
            List<Map<String,Object>> commits = (List<Map<String,Object>>) payload.get("commits");
            String messages = (commits == null ? "" :
                    commits.stream().map(c -> String.valueOf(c.get("message")))
                            .collect(java.util.stream.Collectors.joining("\n")));
            Map<String,Object> pusher = (Map<String,Object>) payload.get("pusher");
            String pusherName = pusher != null ? String.valueOf(pusher.get("name")) : "unknown";

            Map<String,Object> input = new java.util.HashMap<>();
            input.put("repo", fullName);
            input.put("branch", branch);
            input.put("pusher", pusherName);
            input.put("commits", messages);

            // Optional: drive email recipient via workflow var
            // input.put("recipient", "dev-team@example.com");

            workflowRepo.findByName("GitHub Push → Slack+Email").ifPresentOrElse(wf -> {
                try {
                    workflowRuntime.executeWorkflow(wf, input);
                } catch (Exception e) {
                    slackFallback.sendNotification(
                            "Webhook failed to run workflow. Push in " + fullName + " by " + pusherName + ":\n" + messages);
                }
            }, () -> {
                // Fallback if workflow is missing
                slackFallback.sendNotification(
                        "Push in " + fullName + " on " + branch + " by " + pusherName + ":\n" + messages);
            });
        } catch (Exception e) {
            // log error
        }
    }
}