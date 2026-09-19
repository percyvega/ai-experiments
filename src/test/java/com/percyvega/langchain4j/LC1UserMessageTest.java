package com.percyvega.langchain4j;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Log4j2
@Execution(ExecutionMode.CONCURRENT)
class LC1_UserMessageTest {

    @Test
    void anthropic() {
        logResponse("Anthropic", ChatModelFactory.getAnthropic().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void google() {
        logResponse("Google", ChatModelFactory.getGoogle().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void openAi() {
        logResponse("OpenAi", ChatModelFactory.getOpenAi().chat(USER_MESSAGE_TEXT));
    }

    @Test
    void ollama() {
        logResponse("Ollama", ChatModelFactory.getOllama().chat(USER_MESSAGE_TEXT));
    }

    private void logResponse(String provider, String response) {
        log.info("{}: {}", provider, response);
    }
}
