package com.percyvega.raw.impl;

import com.percyvega.raw.AbstractModelHelper;
import com.percyvega.raw.ModelHelper;
import com.percyvega.utils.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

import static com.percyvega.utils.Constants.ANTHROPIC_MODEL_NAME;
import static com.percyvega.utils.Constants.MAX_TOKENS;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;
import static com.percyvega.utils.Constants.TEMPERATURE;

public final class AnthropicHelperImpl extends AbstractModelHelper {

    public static final ModelHelper INSTANCE = new AnthropicHelperImpl();

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
                    "model": "%s",
                    "max_tokens": %d,
                    "temperature": %s,
                    "system": "%s",
                    "messages": [
                        {
                            "role": "user",
                            "content": "%s"
                        }
                    ]
                }
                """.formatted(ANTHROPIC_MODEL_NAME, MAX_TOKENS, TEMPERATURE / 2, SYSTEM_MESSAGE_TEXT, prompt);
    }

    @Override
    public String getPromptResponsePath() {
        return "/content/0/text";
    }
}
