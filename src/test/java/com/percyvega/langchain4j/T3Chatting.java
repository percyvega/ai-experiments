package com.percyvega.langchain4j;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import java.util.List;
import java.util.Scanner;

import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

class T3Chatting {

    public static final ChatModel CHAT_MODEL = ChatModelFactory.getGoogle();
    private static final SystemMessage SYSTEM_MESSAGE = new SystemMessage(SYSTEM_MESSAGE_TEXT);

    void main() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your prompt: ");
            String userInput = scanner.nextLine();
            UserMessage userMessage = UserMessage.from(userInput);

            List<ChatMessage> messages = List.of(SYSTEM_MESSAGE, userMessage);
            ChatResponse chatResponse = CHAT_MODEL.chat(messages);
            System.out.println(chatResponse);
        }
    }
}
