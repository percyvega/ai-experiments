package com.percyvega.raw;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class AbstractAiHelper implements AiHelper {

    @Override
    public final String getResponseFromPrompt(String prompt) {
        return getHttpResponse(getHttpRequest(prompt));
    }

    private String getHttpResponse(HttpRequest httpRequest) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            try {
                HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                return httpResponse.body();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract HttpRequest getHttpRequest(String prompt);

    protected abstract String getBody(String prompt);

}
