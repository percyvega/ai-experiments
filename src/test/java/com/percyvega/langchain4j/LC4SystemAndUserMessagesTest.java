package com.percyvega.langchain4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;
import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Execution(ExecutionMode.CONCURRENT)
class LC4SystemAndUserMessagesTest {

    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);
    private static final UserMessage USER_MESSAGE = new UserMessage(USER_MESSAGE_TEXT);
    private static final List<ChatMessage> CHAT_MESSAGES = List.of(SYSTEM_MESSAGE, USER_MESSAGE);

    @Test
    void anthropic() {
        printResponse("Anthropic", ChatModelFactory.getAnthropic().chat(CHAT_MESSAGES));
    }

    @Test
    void google() {
        printResponse("Google", ChatModelFactory.getGoogle().chat(CHAT_MESSAGES));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", ChatModelFactory.getOpenAi().chat(CHAT_MESSAGES));
    }

    @Test
    void ollama() {
        printResponse("Ollama", ChatModelFactory.getOllama().chat(CHAT_MESSAGES));
    }

    private void printResponse(String provider, ChatResponse chatResponse) {
        IO.println(provider + ": " + chatResponse.aiMessage().text());
    }
}
