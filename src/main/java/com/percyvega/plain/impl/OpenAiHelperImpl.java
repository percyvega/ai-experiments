package com.percyvega.plain.impl;

import com.percyvega.plain.AbstractAiHelper;
import com.percyvega.plain.AiHelper;
import com.percyvega.utils.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

public final class OpenAiHelperImpl extends AbstractAiHelper {

    public static final AiHelper INSTANCE = new OpenAiHelperImpl();

    private OpenAiHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ApiKeys.openAI())
                .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                .build();
    }

    @Override
    protected String getBody(String prompt) {
        return """
                {
                    "model": "gpt-4",
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
