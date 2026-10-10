package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.L2ChatModelFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Execution(ExecutionMode.CONCURRENT)
class LC2UserTextTests {

    @Test
    void anthropic() {
        printResponse("Anthropic", L2ChatModelFactory.getAnthropic().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void google() {
        printResponse("Google", L2ChatModelFactory.getGoogle().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", L2ChatModelFactory.getOpenAi().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void ollama() {
        printResponse("Ollama", L2ChatModelFactory.getOllama().chat(USER_MESSAGE_TEXT));
    }

    private void printResponse(String provider, String response) {
        IO.println(provider + ": " + response);
    }
}
