package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

// LC2 — the same string prompt sent to every provider, each ChatModel now built by ModelFactory.
@Execution(ExecutionMode.CONCURRENT)
class LC2UserTextTests {

    @Test
    void anthropic() {
        printResponse("Anthropic", ModelFactory.getAnthropicChatModel().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void google() {
        printResponse("Google", ModelFactory.getGoogleChatModel().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", ModelFactory.getOpenAiChatModel().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void ollama() {
        printResponse("Ollama", ModelFactory.getOllamaChatModel().chat(USER_MESSAGE_TEXT));
    }

    private void printResponse(String provider, String response) {
        IO.println(provider + ": " + response);
    }
}
