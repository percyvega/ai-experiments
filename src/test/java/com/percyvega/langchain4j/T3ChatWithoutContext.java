package com.percyvega.langchain4j;

import java.util.Scanner;

class T3ChatWithoutContext {

    public static final dev.langchain4j.model.chat.ChatModel CHAT_MODEL = ChatModelFactory.getGoogle();

    static void main() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your message: ");
            String userMessage = scanner.nextLine();
            System.out.println(CHAT_MODEL.chat(userMessage));
        }
    }
}
