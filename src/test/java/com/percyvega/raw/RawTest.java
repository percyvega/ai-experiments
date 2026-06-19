package com.percyvega.raw;

import com.percyvega.raw.impl.AnthropicHelperImpl;
import com.percyvega.raw.impl.GoogleHelperImpl;
import com.percyvega.raw.impl.OllamaHelperImpl;
import com.percyvega.raw.impl.OpenAiHelperImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RawTest {

    private static final Logger log = LogManager.getLogger(RawTest.class);
    private static final String PROMPT = "When is U.S. Independence Day?";

    @Test
    @Order(1)
    void openAi() {
        logResponse(OpenAiHelperImpl.INSTANCE);
    }

    @Test
    @Order(2)
    void google() {
        logResponse(GoogleHelperImpl.INSTANCE);
    }

    @Test
    @Order(3)
    void anthropic() {
        logResponse(AnthropicHelperImpl.INSTANCE);
    }

    @Test
    @Order(4)
    void ollama() {
        logResponse(OllamaHelperImpl.INSTANCE);
    }

    private void logResponse(AiHelper aiHelper) {
//        log.info("\n{}", formatAsJson(aiHelper.getModelResponse(PROMPT)));
        log.info("\n{}", aiHelper.extractPromptResponse(aiHelper.getModelResponse(PROMPT)));
    }
}
