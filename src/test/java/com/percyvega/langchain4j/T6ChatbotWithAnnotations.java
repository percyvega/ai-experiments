package com.percyvega.langchain4j;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import static com.percyvega.utils.Constants.COMMAND_PROMPT;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

class T6ChatbotWithAnnotations {

    private static final ChatModel CHAT_MODEL = ChatModelFactory.getAnthropic();

    private final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    public interface MyChatBot {
        @SystemMessage(SYSTEM_MESSAGE_TEXT)
        @UserMessage("In one short sentence, {{userInput}}")
        ChatResponse sendUserMessage(@V("userInput") String userInput);
    }

    private final MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
            .chatModel(CHAT_MODEL)
            .chatMemory(CHAT_MEMORY)
            .build();

    void main() {
        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            ChatResponse chatResponse = myChatBot.sendUserMessage(userInput);
            IO.println(chatResponse);
        }
    }
}
