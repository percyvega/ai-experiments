package com.percyvega.langchain4j.factory;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;

import java.time.Duration;

import static com.percyvega.utils.Constants.*;

public abstract class L9StreamingChatModelFactory {

    private L9StreamingChatModelFactory() {
    }

    public static StreamingChatModel getAnthropic() {
        return AnthropicStreamingChatModel.builder()
                .apiKey(ApiKeys.anthropic())
                .modelName(ANTHROPIC_MODEL_NAME)
                .temperature(TEMPERATURE / 2)
                .maxTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
}
