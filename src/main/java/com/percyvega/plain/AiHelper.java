package com.percyvega.plain;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface AiHelper {

    default String getResponseFromPrompt(String prompt) {
        return getHttpResponse(getHttpRequest(prompt));
    }

    default String getHttpResponse(HttpRequest httpRequest) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            try {
                HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return httpResponse.body();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    HttpRequest getHttpRequest(String prompt);

    String getBody(String prompt);
}
