package com.percyvega.langchain4j;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.Scanner;

import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

class T4ChattingWithMemory {

    private static final ChatModel CHAT_MODEL = ChatModelFactory.getAnthropic();
    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);

    private final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    void main() {
        CHAT_MEMORY.add(SYSTEM_MESSAGE);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your prompt: ");
            String userInput = scanner.nextLine();
            UserMessage userMessage = UserMessage.from(userInput);
            CHAT_MEMORY.add(userMessage);

            ChatResponse chatResponse = CHAT_MODEL.chat(CHAT_MEMORY.messages());
            CHAT_MEMORY.add(chatResponse.aiMessage());
            System.out.println(chatResponse);
        }
    }
}
