package com.percyvega.raw;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public abstract class AbstractAiHelper implements AiHelper {

    private static final Logger log = LogManager.getLogger(AbstractAiHelper.class);

    @Override
    public final String getResponseFromPrompt(String prompt) {
        return getHttpResponse(getHttpRequest(prompt));
    }

    private String getHttpResponse(HttpRequest httpRequest) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            try {
                StopWatch stopWatch = StopWatch.createStarted();
                HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                stopWatch.stop();
                log.info("Time: {} sec", stopWatch.getTime(TimeUnit.SECONDS));
                return httpResponse.body();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected abstract HttpRequest getHttpRequest(String prompt);

    protected abstract String getBody(String prompt);

}
