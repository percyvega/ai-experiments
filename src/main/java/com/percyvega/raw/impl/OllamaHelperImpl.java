package com.percyvega.raw.impl;

import com.percyvega.raw.AbstractModelHelper;
import com.percyvega.raw.ModelHelper;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.percyvega.utils.Constants.MAX_TOKENS;
import static com.percyvega.utils.Constants.MISTRAL_AI_MODEL_NAME;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;
import static com.percyvega.utils.Constants.TEMPERATURE;

public final class OllamaHelperImpl extends AbstractModelHelper {

    public static final ModelHelper INSTANCE = new OllamaHelperImpl();

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
                    "model": "%s",
                    "max_tokens": %d,
                    "temperature": %s,
                    "messages": [
                        {
                            "role": "system",
                            "content": "%s"
                        },
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ]
                }
                """.formatted(MISTRAL_AI_MODEL_NAME, MAX_TOKENS, TEMPERATURE, SYSTEM_MESSAGE_TEXT, prompt);
    }

    @Override
    public String getPromptResponsePath() {
        return "/choices/0/message/content";
    }
}
