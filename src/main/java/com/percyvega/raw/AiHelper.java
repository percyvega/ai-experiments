package com.percyvega.raw;

public interface AiHelper {

    String getModelResponse(String prompt);
    String extractPromptResponse(String modelResponse);

}
