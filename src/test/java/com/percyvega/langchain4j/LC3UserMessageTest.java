package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ChatModelFactory;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Execution(ExecutionMode.CONCURRENT)
class LC3UserMessageTest {

    private static final UserMessage USER_MESSAGE = new UserMessage(USER_MESSAGE_TEXT);

    @Test
    void anthropic() {
        printResponse("Anthropic", ChatModelFactory.getAnthropic().chat(USER_MESSAGE));
    }

    @Test
    void google() {
        printResponse("Google", ChatModelFactory.getGoogle().chat(USER_MESSAGE));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", ChatModelFactory.getOpenAi().chat(USER_MESSAGE));
    }

    @Test
    void ollama() {
        printResponse("Ollama", ChatModelFactory.getOllama().chat(USER_MESSAGE));
    }

    private void printResponse(String provider, ChatResponse chatResponse) {
        IO.println(provider + ": " + chatResponse);
//        IO.println(provider + ": " + chatResponse.aiMessage());
//        IO.println(provider + ": " + chatResponse.aiMessage().text());
    }
}
