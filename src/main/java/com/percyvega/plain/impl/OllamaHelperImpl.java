package com.percyvega.plain.impl;

import com.percyvega.plain.AbstractAiHelper;
import com.percyvega.plain.AiHelper;

import java.net.URI;
import java.net.http.HttpRequest;

public final class OllamaHelperImpl extends AbstractAiHelper {

    public static final AiHelper INSTANCE = new OllamaHelperImpl();

    private OllamaHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                .build();
    }

    @Override
    protected String getBody(String prompt) {
        return """
                {
                    "model": "qwen3:4b",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ]
                }
                """.formatted(prompt);
    }

}
