package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.*;

import java.util.concurrent.CompletableFuture;

import static com.percyvega.utils.Constants.COMMAND_PROMPT;

// LC9 — StreamingChatModel and TokenStream: the reply is printed piece by piece as it arrives instead of all at once.
class LC9ChatbotStreaming {

    private static final StreamingChatModel CHAT_MODEL = ModelFactory.getAnthropicStreamingChatModel();
    private static final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    private interface MyChatBot {
        @SystemMessage("Be funny and opinionated, but succinct.")
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

            tokenStream.onPartialResponse(IO::print)
                    .onCompleteResponse(future::complete)
                    .onError(future::completeExceptionally)
                    .start();

            future.join();
            IO.println();
        }
    }
}
