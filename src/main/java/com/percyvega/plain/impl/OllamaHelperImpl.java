package com.percyvega.plain.impl;

import com.percyvega.plain.AbstractModelHelper;
import com.percyvega.plain.ModelHelper;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.percyvega.utils.Constants.*;

public final class OllamaHelperImpl extends AbstractModelHelper {

    public static final ModelHelper INSTANCE = new OllamaHelperImpl();

    private OllamaHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(getRequestPayload(prompt)))
                .build();
    }

    @Override
    protected String getRequestPayload(String prompt) {
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
    protected String getPromptResponseJsonPointer() {
        return "/choices/0/message/content";
    }
}
