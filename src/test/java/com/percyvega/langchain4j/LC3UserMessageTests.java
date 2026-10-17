package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

// The prompt wrapped in a UserMessage instead of passed as a bare string,
// so chat() returns a whole ChatResponse instead of just the text.
@Execution(ExecutionMode.CONCURRENT)
class LC3UserMessageTests {

    private static final UserMessage USER_MESSAGE = UserMessage.from(USER_MESSAGE_TEXT);

    @Test
    void anthropic() {
        printResponse("Anthropic", ModelFactory.getAnthropicChatModel().chat(USER_MESSAGE));
    }

    @Test
    void google() {
        printResponse("Google", ModelFactory.getGoogleChatModel().chat(USER_MESSAGE));
    }

    @Test
    void openAi() {
        printResponse("OpenAi", ModelFactory.getOpenAiChatModel().chat(USER_MESSAGE));
    }

    @Test
    void ollama() {
        printResponse("Ollama", ModelFactory.getOllamaChatModel().chat(USER_MESSAGE));
    }

    private void printResponse(String provider, ChatResponse chatResponse) {
        IO.println(provider + ": " + chatResponse);
//        IO.println(provider + ": " + chatResponse.aiMessage());
//        IO.println(provider + ": " + chatResponse.aiMessage().text());
    }
}
