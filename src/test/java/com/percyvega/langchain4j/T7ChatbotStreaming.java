package com.percyvega.langchain4j;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.*;

import java.util.concurrent.CompletableFuture;

import static com.percyvega.utils.Constants.COMMAND_PROMPT;
import static com.percyvega.utils.Constants.SYSTEM_MESSAGE_TEXT;

class T7ChatbotStreaming {

    private static final StreamingChatModel CHAT_MODEL = StreamingChatModelFactory.getAnthropic();

    private final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    public interface MyChatBot {
        @SystemMessage(SYSTEM_MESSAGE_TEXT)
        @UserMessage("Use bullet points: {{userInput}}")
        TokenStream sendUserMessage(@V("userInput") String userInput);
    }

    private final MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
            .streamingChatModel(CHAT_MODEL)
            .chatMemory(CHAT_MEMORY)
            .build();

    void main() {
        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            CompletableFuture<ChatResponse> future = new CompletableFuture<>();
            TokenStream tokenStream = myChatBot.sendUserMessage(userInput);

            tokenStream.onPartialResponse(System.out::print)
                    .onCompleteResponse(future::complete)
                    .onError(future::completeExceptionally)
                    .start();

            future.join();
            IO.println();
        }
    }
}
