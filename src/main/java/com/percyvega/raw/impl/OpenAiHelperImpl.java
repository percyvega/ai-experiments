package com.percyvega.raw.impl;

import com.percyvega.raw.AbstractModelHelper;
import com.percyvega.raw.ModelHelper;
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
                .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                .build();
    }

    @Override
    protected String getBody(String prompt) {
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
    public String getPromptResponsePath() {
        return "/choices/0/message/content";
    }
}
