package com.percyvega.langchain4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

class UserMessageTest {

    private static final Logger log = LogManager.getLogger(UserMessageTest.class);

    private static final String PROMPT = "I bought a new bulldog. Please suggest a name for him.";

    @Test
    void anthropic() {
        logResponse(ChatModelFactory.getAnthropic().chat(PROMPT));
    }

    @Test
    void google() {
        logResponse(ChatModelFactory.getGoogle().chat(PROMPT));
    }

    @Test
    void ollama() {
        logResponse(ChatModelFactory.getOllama().chat(PROMPT));
    }

    @Test
    void openAi() {
        logResponse(ChatModelFactory.getOpenAi().chat(PROMPT));
    }

    private void logResponse(String response) {
        log.info("\n{}", response);
    }
}
