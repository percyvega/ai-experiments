package com.percyvega.plain.impl;

import com.percyvega.plain.AbstractAiHelper;
import com.percyvega.plain.AiHelper;
import com.percyvega.utils.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

public final class GoogleHelperImpl extends AbstractAiHelper {

    public static final AiHelper INSTANCE = new GoogleHelperImpl();

    private GoogleHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", ApiKeys.google())
                .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                .build();
    }

    @Override
    protected String getBody(String prompt) {
        return """
                {
                    "contents": [
                        {
                            "parts": [
                                { "text": "%s" }
                            ]
                        }
                    ]
                }
                """.formatted(prompt);
    }
}
