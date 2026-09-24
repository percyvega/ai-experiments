package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ChatModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Execution(ExecutionMode.CONCURRENT)
class LC2AllModelsUserTextTest {

    @Test
    void anthropic() {
        printResponse("Anthropic", ChatModelFactory.getAnthropic().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void google() {
        printResponse("Google", ChatModelFactory.getGoogle().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", ChatModelFactory.getOpenAi().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void ollama() {
        printResponse("Ollama", ChatModelFactory.getOllama().chat(USER_MESSAGE_TEXT));
    }

    private void printResponse(String provider, String response) {
        IO.println(provider + ": " + response);
    }
}
