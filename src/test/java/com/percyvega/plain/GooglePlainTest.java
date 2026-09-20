package com.percyvega.plain;

import com.percyvega.plain.util.JsonUtils;
import com.percyvega.utils.ApiKeys;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static com.percyvega.utils.Constants.*;

class GooglePlainTest {

    @Test
    void google() {
        try (HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build()) {
            try {
                HttpResponse<String> httpResponse = client.send(getHttpRequest(), HttpResponse.BodyHandlers.ofString());

                String modelResponse = httpResponse.body();
                if (httpResponse.statusCode() != 200) {
                    throw new RuntimeException(modelResponse);
                }

                IO.println(modelResponse);
                // IO.println(JsonUtils.getValue(modelResponse, getPromptResponseJsonPointer()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private HttpRequest getHttpRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + GOOGLE_AI_MODEL_NAME + ":generateContent"))
                .header("Content-Type", "application/json")
                .header("x-goog-api-key", ApiKeys.google())
                .POST(HttpRequest.BodyPublishers.ofString(getRequestPayload()))
                .build();
    }

    private String getRequestPayload() {
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
                """.formatted(SYSTEM_MESSAGE_TEXT, USER_MESSAGE_TEXT, MAX_TOKENS, TEMPERATURE);
    }

    protected String getPromptResponseJsonPointer() {
        return "/candidates/0/content/parts/0/text";
    }
}
