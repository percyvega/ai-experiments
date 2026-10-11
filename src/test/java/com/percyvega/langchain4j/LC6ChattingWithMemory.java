package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

import static com.percyvega.utils.Constants.COMMAND_PROMPT;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

// LC6 — a MessageWindowChatMemory that keeps the last 10 messages, fed by hand with each question and reply
// and sent in full on every turn, so the model remembers the conversation.
class LC6ChattingWithMemory {

    private static final ChatModel CHAT_MODEL = ModelFactory.getAnthropicChatModel();
    private static final SystemMessage SYSTEM_MESSAGE = SystemMessage.from(SYSTEM_MESSAGE_TEXT);

    private final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    void main() {
        CHAT_MEMORY.add(SYSTEM_MESSAGE);

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            UserMessage userMessage = UserMessage.from(userInput);
            CHAT_MEMORY.add(userMessage);

            ChatResponse chatResponse = CHAT_MODEL.chat(CHAT_MEMORY.messages());
            CHAT_MEMORY.add(chatResponse.aiMessage());
            IO.println(chatResponse.aiMessage().text());
        }
    }
}
