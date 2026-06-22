package com.percyvega.langchain4j;

import dev.langchain4j.model.chat.ChatModel;

import java.util.Scanner;

class T4ChatWithContext {

    public static final ChatModel CHAT_MODEL = ChatModelFactory.getGoogle();

    void main() {
        System.out.println("Enter '/exit' to quit.");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your message: ");
            String prompt = scanner.nextLine();
            if ("/exit".equalsIgnoreCase(prompt)) {
                break;
            }
            String chatResponse = CHAT_MODEL.chat(prompt);
            System.out.println(chatResponse);
        }
    }
}
