package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;

import static com.percyvega.utils.Constants.COMMAND_PROMPT;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

// LC7 — the same memory through AiServices: declare an interface, and LangChain4j implements it,
// storing each message and reply in the memory for you.
class LC7Chatbot {

    private static final ChatModel CHAT_MODEL = ModelFactory.getAnthropicChatModel();
    private static final SystemMessage SYSTEM_MESSAGE = SystemMessage.from(SYSTEM_MESSAGE_TEXT);
    private static final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    private interface MyChatBot {
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
//            IO.println(chatResponse.aiMessage());
//            IO.println(chatResponse.aiMessage().text());
        }
    }
}
