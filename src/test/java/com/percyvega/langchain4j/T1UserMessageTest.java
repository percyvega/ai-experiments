package com.percyvega.langchain4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class T1UserMessageTest {

    private static final Logger log = LogManager.getLogger(T1UserMessageTest.class);

    private static final String PROMPT = "When is the U.S. Independence Day?";

    @Test
    @Order(1)
    void openAi() {
        logResponse(ChatModelFactory.getOpenAi().chat(PROMPT));
    }

    @Test
    @Order(2)
    void anthropic() {
        logResponse(ChatModelFactory.getAnthropic().chat(PROMPT));
    }

    @Test
    @Order(3)
    void google() {
        logResponse(ChatModelFactory.getGoogle().chat(PROMPT));
    }

    @Test
    @Order(4)
    void ollama() {
        logResponse(ChatModelFactory.getOllama().chat(PROMPT));
    }

    private void logResponse(String response) {
        log.info("\n{}", response);
    }
}
