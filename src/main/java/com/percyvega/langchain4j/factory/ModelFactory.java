package com.percyvega.langchain4j.factory;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

import java.time.Duration;

import static com.percyvega.utils.Constants.*;

public abstract class ModelFactory {

    private ModelFactory() {
    }

    public static ChatModel getAnthropicChatModel() {
        return AnthropicChatModel.builder()
                .apiKey(ApiKeys.anthropic())
                .modelName(ANTHROPIC_CHAT_MODEL_NAME)
                .temperature(TEMPERATURE / 2)
                .maxTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
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

    public static ChatModel getOpenAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(ApiKeys.openAI())
                .modelName(OPENAI_CHAT_MODEL_NAME)
                .temperature(TEMPERATURE)
                .maxCompletionTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static ChatModel getOllamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(MISTRAL_CHAT_MODEL_NAME)
                .temperature(TEMPERATURE)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static StreamingChatModel getAnthropicStreamingChatModel() {
        return AnthropicStreamingChatModel.builder()
                .apiKey(ApiKeys.anthropic())
                .modelName(ANTHROPIC_CHAT_MODEL_NAME)
                .temperature(TEMPERATURE / 2)
                .maxTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static EmbeddingModel getOpenAiEmbeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(ApiKeys.openAI())
                .modelName(OPENAI_EMBEDDING_MODEL_NAME)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
}
