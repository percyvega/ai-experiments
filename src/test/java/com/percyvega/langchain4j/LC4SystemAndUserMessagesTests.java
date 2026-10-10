package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.L2ChatModelFactory;
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
class LC4SystemAndUserMessagesTests {

    private static final SystemMessage SYSTEM_MESSAGE = SystemMessage.from(SYSTEM_MESSAGE_TEXT);
    private static final UserMessage USER_MESSAGE = UserMessage.from(USER_MESSAGE_TEXT);
    private static final List<ChatMessage> CHAT_MESSAGES = List.of(SYSTEM_MESSAGE, USER_MESSAGE);

    @Test
    void anthropic() {
        printResponse("Anthropic", L2ChatModelFactory.getAnthropic().chat(CHAT_MESSAGES));
    }

    @Test
    void google() {
        printResponse("Google", L2ChatModelFactory.getGoogle().chat(CHAT_MESSAGES));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", L2ChatModelFactory.getOpenAi().chat(CHAT_MESSAGES));
    }

    @Test
    void ollama() {
        printResponse("Ollama", L2ChatModelFactory.getOllama().chat(CHAT_MESSAGES));
    }

    private void printResponse(String provider, ChatResponse chatResponse) {
        IO.println(provider + ": " + chatResponse.aiMessage().text());
    }
}
