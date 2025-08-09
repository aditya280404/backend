package com.example.automation.service.ai;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Workflow node that summarises a block of text by returning the first few
 * sentences.  This is a trivial implementation used for demonstration; you
 * could swap this out for a call to a machine‑learning model or AI service.
 */
public class TextSummarizationNode implements WorkflowNode {
    private final int sentenceCount;

    public TextSummarizationNode() {
        this(3);
    }

    public TextSummarizationNode(int sentenceCount) {
        this.sentenceCount = sentenceCount;
    }

    @Override
    public Object execute(Object input) throws Exception {
        if (!(input instanceof String)) {
            throw new IllegalArgumentException("TextSummarizationNode requires a String input");
        }
        String text = (String) input;
        List<String> sentences = Arrays.stream(text.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        return sentences.stream().limit(sentenceCount).collect(Collectors.joining(" "));
    }
}