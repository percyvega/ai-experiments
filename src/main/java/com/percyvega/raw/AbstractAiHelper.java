package com.percyvega.raw;

import com.percyvega.utils.Utils;
import dev.langchain4j.exception.AuthenticationException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class AbstractAiHelper implements AiHelper {

    @Override
    public final String getModelResponse(String prompt) {
        return getHttpResponse(getHttpRequest(prompt));
    }

    @Override
    public final String extractPromptResponse(String modelResponse) {
        return Utils.getValue(modelResponse, getPromptResponsePath());
    }

    protected abstract String getPromptResponsePath();

    private String getHttpResponse(HttpRequest httpRequest) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            try {
                HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                if (httpResponse.statusCode() != 200) {
                    throw new AuthenticationException(getBodyErrorMessage(httpResponse));
                }

                return httpResponse.body();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getBodyErrorMessage(HttpResponse<String> httpResponse) {
        return httpResponse.body();
    }

    protected abstract HttpRequest getHttpRequest(String prompt);

    protected abstract String getBody(String prompt);

}
