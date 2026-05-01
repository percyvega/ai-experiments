package com.percyvega.langchain4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class T2UserAndSystemMessagesTest {

    private static final Logger log = LogManager.getLogger(T2UserAndSystemMessagesTest.class);

    private static final SystemMessage systemMessage = new SystemMessage("You are an extremely funny and opinionated individual.");
    private static final UserMessage userMessage = new UserMessage("When is the U.S. Independence Day?");
    private static final List<ChatMessage> MESSAGES = List.of(systemMessage, userMessage);

    @Test
    @Order(1)
    void openAi() {
        logResponse(ChatModelFactory.getOpenAi().chat(MESSAGES));
    }

    @Test
    @Order(2)
    void anthropic() {
        logResponse(ChatModelFactory.getAnthropic().chat(MESSAGES));
    }

    @Test
    @Order(3)
    void google() {
        logResponse(ChatModelFactory.getGoogle().chat(MESSAGES));
    }

    @Test
    @Order(4)
    void ollama() {
        logResponse(ChatModelFactory.getOllama().chat(MESSAGES));
    }

    private void logResponse(ChatResponse chatResponse) {
        log.info("\n{}", chatResponse);
    }
}
