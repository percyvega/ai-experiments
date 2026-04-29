package com.percyvega.raw;

import com.percyvega.raw.impl.AnthropicHelperImpl;
import com.percyvega.raw.impl.GoogleHelperImpl;
import com.percyvega.raw.impl.OllamaHelperImpl;
import com.percyvega.raw.impl.OpenAiHelperImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static com.percyvega.utils.Utils.formatAsJson;

class RawTest {

    private static final Logger log = LogManager.getLogger(RawTest.class);
    private static final String PROMPT = "I bought a new bulldog. Please suggest a name for him.";

    @Test
    void anthropic() {
        logResponse(AnthropicHelperImpl.INSTANCE); // 4 seconds
    }

    @Test
    void google() {
        logResponse(GoogleHelperImpl.INSTANCE); // 4 seconds
    }

    @Test
    void ollama() {
        logResponse(OllamaHelperImpl.INSTANCE); // 31 seconds
    }

    @Test
    void openAi() {
        logResponse(OpenAiHelperImpl.INSTANCE); // 2 seconds
    }

    private void logResponse(AiHelper aiHelper) {
        log.info("\n{}", formatAsJson(aiHelper.getResponseFromPrompt(PROMPT)));
    }
}
