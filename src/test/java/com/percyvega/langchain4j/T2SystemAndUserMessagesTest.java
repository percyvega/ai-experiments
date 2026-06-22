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

import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;
import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class T2SystemAndUserMessagesTest {

    private static final Logger log = LogManager.getLogger(T2SystemAndUserMessagesTest.class);

    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);
    private static final UserMessage USER_MESSAGE = new UserMessage(USER_MESSAGE_TEXT);
    private static final List<ChatMessage> CHAT_MESSAGES = List.of(SYSTEM_MESSAGE, USER_MESSAGE);

    @Test
    @Order(1)
    void anthropic() {
        logResponse(ChatModelFactory.getAnthropic().chat(CHAT_MESSAGES));
    }

    @Test
    @Order(2)
    void google() {
        logResponse(ChatModelFactory.getGoogle().chat(CHAT_MESSAGES));
    }

    @Test
    @Order(3)
    void openAi() {
        logResponse(ChatModelFactory.getOpenAi().chat(CHAT_MESSAGES));
    }

    @Test
    @Order(4)
    void ollama() {
        logResponse(ChatModelFactory.getOllama().chat(CHAT_MESSAGES));
    }

    private void logResponse(ChatResponse chatResponse) {
        log.info("\n{}", chatResponse);
//        log.info("\n{}", chatResponse.aiMessage().text());
    }
}
