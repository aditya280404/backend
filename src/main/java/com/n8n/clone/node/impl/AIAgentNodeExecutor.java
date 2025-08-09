package com.n8n.clone.node.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.n8n.clone.node.NodeExecutionContext;
import com.n8n.clone.node.NodeExecutionResult;
import com.n8n.clone.node.NodeExecutor;

import jakarta.annotation.PostConstruct;

/**
 * Node that delegates natural language processing to an AI service (Groq
 * compatible OpenAI API).  It accepts parameters such as {@code prompt},
 * {@code model}, {@code maxTokens} and {@code temperature}, substitutes
 * variables in the prompt from the workflow input and returns the AI
 * response as part of the node's output.
 */
@Component
public class AIAgentNodeExecutor implements NodeExecutor {

    @Value("${spring.ai.openai.api-key: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}")
    private String groqApiKey;

    @Value("${spring.ai.openai.base-url:https://api.groq.com/openai}")
    private String groqBaseUrl;

    @Value("${spring.ai.openai.chat.model:llama3-70b-8192}")
    private String defaultModel;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(groqBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + groqApiKey)
                .build();
    }

    @Override
    public String getNodeType() {
        return "ai-agent";
    }

    @Override
    public NodeExecutionResult execute(NodeExecutionContext context) throws Exception {
        try {
            // Retrieve parameters; fallback to defaults
            String prompt = (String) context.getParameter("prompt");
            String model = (String) context.getParameter("model");
            Integer maxTokens = (Integer) context.getParameter("maxTokens");
            Double temperature = (Double) context.getParameter("temperature");
            if (model == null) model = defaultModel;
            if (maxTokens == null) maxTokens = 150;
            if (temperature == null) temperature = 0.7;

            // Replace variables in prompt (e.g. {{variableName}}) with values from input data
            String processedPrompt = processPromptVariables(prompt, context.getInputData());

            // Prepare payload for OpenAI/Groq API
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", model);
            payload.put("messages", List.of(Map.of(
                    "role", "user",
                    "content", processedPrompt
            )));
            payload.put("max_tokens", maxTokens);
            payload.put("temperature", temperature);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            // Parse AI response
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            @SuppressWarnings("unchecked")
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String aiResponse = (String) message.get("content");

            Map<String, Object> outputData = new HashMap<>();
            outputData.put("response", aiResponse);
            outputData.put("prompt", processedPrompt);
            outputData.put("model", model);
            outputData.put("usage", response.get("usage"));

            return NodeExecutionResult.success(outputData);
        } catch (Exception e) {
            return NodeExecutionResult.error("Failed to execute AI agent with Groq: " + e.getMessage());
        }
    }

    /**
     * Substitute variables in the prompt with values from the input data.
     * Placeholders in the form {{variableName}} will be replaced by the
     * corresponding entry in {@code inputData} if present.
     */
    private String processPromptVariables(String prompt, Map<String, Object> inputData) {
        if (prompt == null || inputData == null) {
            return prompt;
        }
        String processed = prompt;
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            processed = processed.replace(placeholder, String.valueOf(entry.getValue()));
        }
        return processed;
    }
}