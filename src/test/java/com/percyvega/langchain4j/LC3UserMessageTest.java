package com.percyvega.langchain4j;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

@Log4j2
@Execution(ExecutionMode.CONCURRENT)
class LC3UserMessageTest {

    private static final UserMessage USER_MESSAGE = new UserMessage(USER_MESSAGE_TEXT);

    @Test
    void anthropic() {
        logResponse("Anthropic", ChatModelFactory.getAnthropic().chat(USER_MESSAGE));
    }

    @Test
    void google() {
        logResponse("Google", ChatModelFactory.getGoogle().chat(USER_MESSAGE));
    }

    @Test
    void openAi() {
        logResponse("OpenAi", ChatModelFactory.getOpenAi().chat(USER_MESSAGE));
    }

    @Test
    void ollama() {
        logResponse("Ollama", ChatModelFactory.getOllama().chat(USER_MESSAGE));
    }

    private void logResponse(String provider, ChatResponse chatResponse) {
        log.info("{}: {}", provider, chatResponse);
//        log.info("{}: {}", provider, chatResponse.aiMessage());
//        log.info("{}: {}", provider, chatResponse.aiMessage().text());
    }
}
