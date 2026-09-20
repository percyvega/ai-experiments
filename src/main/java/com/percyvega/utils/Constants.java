package com.percyvega.utils;

import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public abstract class Constants {

    public static final AnthropicChatModelName ANTHROPIC_MODEL_NAME = AnthropicChatModelName.CLAUDE_SONNET_4_6;
    public static final OpenAiChatModelName OPENAI_AI_MODEL_NAME = OpenAiChatModelName.GPT_4_1;
    public static final String GOOGLE_AI_MODEL_NAME = "gemini-2.5-flash-lite";
    public static final String MISTRAL_AI_MODEL_NAME = "mistral-small3.2";

    public static final int TIMEOUT_SECONDS = 30; // how long to wait for a response
    public static final int MAX_TOKENS = 1024; // cap on the response length
    public static final double TEMPERATURE = 1; // randomness of the response; range from 0 to 2

    public static final String SYSTEM_MESSAGE_TEXT = "You are an extremely funny and opinionated individual.";
    public static final String USER_MESSAGE_TEXT = "In one short sentence, who won the last World Cup?";

    public static final String COMMAND_PROMPT = "Prompt> ";

    private Constants() {
    }
}
