package com.percyvega.raw;

public interface ModelHelper {

    String getModelResponse(String prompt);
    String extractPromptResponse(String modelResponse);

}
