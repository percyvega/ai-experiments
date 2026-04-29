package com.percyvega.langchain4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

// run these in parallel
class UserAndSystemMessagesTest {

    private static final Logger log = LogManager.getLogger(UserAndSystemMessagesTest.class);

    private static final SystemMessage systemMessage = new SystemMessage("You are a very funny individual.");
    private static final UserMessage userMessage = new UserMessage("I bought a new bulldog. Please suggest a name for him.");
    private static final List<ChatMessage> MESSAGES = List.of(systemMessage, userMessage);

    @Test
    void anthropic() {
        logResponse(ChatModelFactory.getAnthropic().chat(MESSAGES));
    }

    @Test
    void google() {
        logResponse(ChatModelFactory.getGoogle().chat(MESSAGES));
    }

    @Test
    void openAi() {
        logResponse(ChatModelFactory.getOpenAi().chat(MESSAGES));
    }

    @Test
    void ollama() {
        logResponse(ChatModelFactory.getOllama().chat(MESSAGES));
    }

    private void logResponse(ChatResponse chatResponse) {
        log.info("\n{}", chatResponse);
    }
}
