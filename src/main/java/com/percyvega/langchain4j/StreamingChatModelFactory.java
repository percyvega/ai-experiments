package com.percyvega.langchain4j;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.time.Duration;

import static com.percyvega.utils.Constants.*;

public abstract class StreamingChatModelFactory {

    private StreamingChatModelFactory() {
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

    public static StreamingChatModel getGoogle() {
        return GoogleAiGeminiStreamingChatModel.builder()
                .apiKey(ApiKeys.google())
                .modelName(GOOGLE_AI_MODEL_NAME)
                .temperature(TEMPERATURE)
                .maxOutputTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static StreamingChatModel getOpenAi() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.openAI())
                .modelName(OPENAI_AI_MODEL_NAME)
                .temperature(TEMPERATURE)
                .maxCompletionTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static StreamingChatModel getOllama() {
        return OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(MISTRAL_AI_MODEL_NAME)
                .temperature(TEMPERATURE)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
}
