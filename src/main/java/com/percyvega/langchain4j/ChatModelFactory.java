package com.percyvega.langchain4j;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

import static com.percyvega.utils.Constants.*;

public abstract class ChatModelFactory {

    private ChatModelFactory() {
    }

    public static ChatModel getAnthropic() {
        return AnthropicChatModel.builder()
                .apiKey(ApiKeys.anthropic())
                .modelName(ANTHROPIC_MODEL_NAME)
                .temperature(TEMPERATURE / 2)
                .maxTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static ChatModel getGoogle() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(ApiKeys.google())
                .modelName(GOOGLE_AI_MODEL_NAME)
                .temperature(TEMPERATURE)
                .maxOutputTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static ChatModel getOpenAi() {
        return OpenAiChatModel.builder()
                .apiKey(ApiKeys.openAI())
                .modelName(OPENAI_AI_MODEL_NAME)
                .temperature(TEMPERATURE)
                .maxCompletionTokens(MAX_TOKENS)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

    public static ChatModel getOllama() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(MISTRAL_AI_MODEL_NAME)
                .temperature(TEMPERATURE)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }
}
