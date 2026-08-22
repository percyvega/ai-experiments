package com.percyvega.langchain4j;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;

import static com.percyvega.utils.Constants.COMMAND_PROMPT;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

class T5Chatbot {

    private static final ChatModel CHAT_MODEL = ChatModelFactory.getAnthropic();
    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);

    private final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    public interface MyChatBot {
        ChatResponse sendUserMessage(UserMessage userMessage);
    }

    private final MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
            .chatModel(CHAT_MODEL)
            .chatMemory(CHAT_MEMORY)
            .build();

    void main() {
        CHAT_MEMORY.add(SYSTEM_MESSAGE);

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            UserMessage userMessage = UserMessage.from(userInput);

            ChatResponse chatResponse = myChatBot.sendUserMessage(userMessage);
            IO.println(chatResponse);
        }
    }
}
