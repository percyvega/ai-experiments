package com.percyvega.langchain4j;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

class T5Chatbot {

    public static final ChatModel CHAT_MODEL = ChatModelFactory.getAnthropic();
    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);

    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

    public interface MyChatBot {
        ChatResponse sendUserMessage(UserMessage userMessage);
    }

    private final MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
            .chatModel(CHAT_MODEL)
            .chatMemory(chatMemory)
            .build();

    void main() {
        chatMemory.add(SYSTEM_MESSAGE);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your prompt: ");
            String userInput = scanner.nextLine();
            UserMessage userMessage = UserMessage.from(userInput);

            ChatResponse chatResponse = myChatBot.sendUserMessage(userMessage);
            System.out.println(chatResponse);
        }
    }
}
