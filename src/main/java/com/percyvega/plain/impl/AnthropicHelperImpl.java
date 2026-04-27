package com.percyvega.plain.impl;

import com.percyvega.plain.AbstractAiHelper;
import com.percyvega.plain.AiHelper;
import com.percyvega.utils.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

public final class AnthropicHelperImpl extends AbstractAiHelper {

    public static final AiHelper INSTANCE = new AnthropicHelperImpl();

    private AnthropicHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", ApiKeys.anthropic())
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                .build();
    }

    @Override
    protected String getBody(String prompt) {
        return """
                {
                    "model": "claude-sonnet-4-6",
                    "max_tokens": 1024,
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
