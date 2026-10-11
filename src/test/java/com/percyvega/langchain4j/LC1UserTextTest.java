package com.percyvega.langchain4j;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.percyvega.utils.Constants.*;

// LC1 — first contact through LangChain4j: a GoogleAiGeminiChatModel built inline and sent one plain string prompt.
class LC1UserTextTest {

    @Test
    void google() {
        IO.println(getGoogleChatModel().chat(USER_MESSAGE_TEXT));
    }

    public static ChatModel getGoogleChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(ApiKeys.google())
                .modelName(GOOGLE_CHAT_MODEL_NAME)
                .temperature(TEMPERATURE)
                .maxOutputTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
}
