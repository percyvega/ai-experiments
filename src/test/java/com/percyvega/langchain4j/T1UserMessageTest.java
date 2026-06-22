package com.percyvega.langchain4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class T1UserMessageTest {

    private static final Logger log = LogManager.getLogger(T1UserMessageTest.class);

    @Test
    @Order(1)
    void anthropic() {
        logResponse(ChatModelFactory.getAnthropic().chat(USER_MESSAGE_TEXT));
    }

    @Test
    @Order(2)
    void google() {
        logResponse(ChatModelFactory.getGoogle().chat(USER_MESSAGE_TEXT));
    }

    @Test
    @Order(3)
    void openAi() {
        logResponse(ChatModelFactory.getOpenAi().chat(USER_MESSAGE_TEXT));
    }

    @Test
    @Order(4)
    void ollama() {
        logResponse(ChatModelFactory.getOllama().chat(USER_MESSAGE_TEXT));
    }

    private void logResponse(String response) {
        log.info("\n{}", response);
    }
}
