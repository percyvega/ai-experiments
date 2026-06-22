package com.percyvega.raw.impl;

import com.percyvega.raw.AbstractModelHelper;
import com.percyvega.raw.ModelHelper;
import com.percyvega.utils.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.percyvega.utils.Constants.GOOGLE_AI_MODEL_NAME;
import static com.percyvega.utils.Constants.MAX_TOKENS;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;
import static com.percyvega.utils.Constants.TEMPERATURE;

public final class GoogleHelperImpl extends AbstractModelHelper {

    public static final ModelHelper INSTANCE = new GoogleHelperImpl();

    private GoogleHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + GOOGLE_AI_MODEL_NAME + ":generateContent"))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", ApiKeys.google())
                .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                .build();
    }

    @Override
    protected String getBody(String prompt) {
        return """
                {
                    "systemInstruction": {
                        "parts": [
                            { "text": "%s" }
                        ]
                    },
                    "contents": [
                        {
                            "parts": [
                                { "text": "%s" }
                            ]
                        }
                    ],
                    "generationConfig": {
                        "maxOutputTokens": %d,
                        "temperature": %s
                    }
                }
                """.formatted(SYSTEM_MESSAGE_TEXT, prompt, MAX_TOKENS, TEMPERATURE);
    }

    @Override
    public String getPromptResponsePath() {
        return "/candidates/0/content/parts/0/text";
    }
}
