package com.percyvega.plain;

import com.percyvega.ApiKeys;

import java.net.URI;
import java.net.http.HttpRequest;

public enum AiProvider implements AiHelper {

    OPENAI {
        @Override
        public HttpRequest getHttpRequest(String prompt) {
            return HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + ApiKeys.openAI())
                    .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                    .build();
        }

        @Override
        public String getBody(String prompt) {
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
    },

    ANTHROPIC {
        @Override
        public HttpRequest getHttpRequest(String prompt) {
            return HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("Content-Type", "application/json")
                    .header("x-api-key", ApiKeys.anthropic())
                    .header("anthropic-version", "2023-06-01")
                    .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                    .build();
        }

        @Override
        public String getBody(String prompt) {
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
    },

    GOOGLE {
        @Override
        public HttpRequest getHttpRequest(String prompt) {
            return HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", ApiKeys.google())
                    .POST(HttpRequest.BodyPublishers.ofString(getBody(prompt)))
                    .build();
        }

        @Override
        public String getBody(String prompt) {
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
}
