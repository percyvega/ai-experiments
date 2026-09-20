package com.percyvega.langchain4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;
import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Log4j2
@Execution(ExecutionMode.CONCURRENT)
class LC3SystemAndUserMessagesTest {

    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);
    private static final UserMessage USER_MESSAGE = new UserMessage(USER_MESSAGE_TEXT);
    private static final List<ChatMessage> CHAT_MESSAGES = List.of(SYSTEM_MESSAGE, USER_MESSAGE);

    @Test
    void anthropic() {
        logResponse("Anthropic", ChatModelFactory.getAnthropic().chat(CHAT_MESSAGES));
    }

    @Test
    void google() {
        logResponse("Google", ChatModelFactory.getGoogle().chat(CHAT_MESSAGES));
    }

    @Test
    void openAi() {
        logResponse("OpenAi", ChatModelFactory.getOpenAi().chat(CHAT_MESSAGES));
    }

    @Test
    void ollama() {
        logResponse("Ollama", ChatModelFactory.getOllama().chat(CHAT_MESSAGES));
    }

    private void logResponse(String provider, ChatResponse chatResponse) {
        log.info("{}: {}", provider, chatResponse.aiMessage().text());
    }
}
