package com.percyvega.langchain4j;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.time.Duration;

public abstract class ChatModelFactory {

    private ChatModelFactory() {
    }

    public static ChatModel getAnthropic() {
        return AnthropicChatModel.builder()
                .apiKey(ApiKeys.anthropic())
                .modelName(AnthropicChatModelName.CLAUDE_SONNET_4_5_20250929)
                .temperature(1.0)
                .maxTokens(1024)
                .timeout(Duration.ofSeconds(30))
                .build();
    }

    public static ChatModel getOpenAi() {
        return OpenAiChatModel.builder()
                .apiKey(ApiKeys.openAI())
                .modelName(OpenAiChatModelName.GPT_4_1_MINI)
                .temperature(1.0)
                .maxCompletionTokens(1024)
                .timeout(Duration.ofSeconds(30))
                .build();
    }

    public static ChatModel getGoogle() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(ApiKeys.google())
                .modelName("gemini-2.5-flash")
                .temperature(1.0)
                .maxOutputTokens(1024)
                .timeout(Duration.ofSeconds(30))
                .build();
    }

    public static ChatModel getOllama() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("mistral-small3.2")
                .temperature(1.0)
                .timeout(Duration.ofSeconds(300))
                .build();
    }
}
