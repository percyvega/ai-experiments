package com.percyvega.plain.impl;

import com.percyvega.plain.AbstractModelHelper;
import com.percyvega.plain.ModelHelper;
import com.percyvega.utils.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.percyvega.utils.Constants.*;

public final class OpenAiHelperImpl extends AbstractModelHelper {

    public static final ModelHelper INSTANCE = new OpenAiHelperImpl();

    private OpenAiHelperImpl() {
    }

    @Override
    protected HttpRequest getHttpRequest(String prompt) {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + ApiKeys.openAI())
                .POST(HttpRequest.BodyPublishers.ofString(getRequestPayload(prompt)))
                .build();
    }

    @Override
    protected String getRequestPayload(String prompt) {
        return """
                {
                    "model": "%s",
                    "max_completion_tokens": %d,
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
                """.formatted(OPENAI_AI_MODEL_NAME, MAX_TOKENS, TEMPERATURE, SYSTEM_MESSAGE_TEXT, prompt);
    }

    @Override
    protected String getPromptResponseJsonPointer() {
        return "/choices/0/message/content";
    }
}
