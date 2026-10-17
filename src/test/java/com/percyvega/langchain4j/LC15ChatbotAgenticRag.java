package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;
import java.util.stream.Collectors;

// The model gets searchJavaNotes(query) as a tool and writes its own queries.
// It may split one question into several searches, retry with different words when a search misses, or skip searching entirely.
// Each call prints a [tool] line so you can watch it decide. The system message tells it to answer only from the notes.
class LC15ChatbotAgenticRag {

    private static final ChatModel CHAT_MODEL = ModelFactory.getAnthropicChatModel();

    private static final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);

    static class JavaNoteTools {
        private static final EmbeddingModel EMBEDDING_MODEL = ModelFactory.getOpenAiEmbeddingModel();

        private static final String FILE_NAME = "/introduction-to-java.txt";

        private static final EmbeddingStore<TextSegment> EMBEDDING_STORE = new InMemoryEmbeddingStore<>();

        static {
            List<String> lines = FileUtils.getLines(FILE_NAME);
            EmbeddingUtils.getEmbeddings(EMBEDDING_MODEL, lines)
                    .forEach((line, embedding) -> EMBEDDING_STORE.add(embedding, TextSegment.from(line)));
        }

        // The @Tool and @P descriptions are written for the Model to decide when/how to use this, not for humans. This covers what the tool does and returns.
        @Tool("Searches the Java notes by meaning and returns the most similar question-and-answer lines")
        String searchJavaNotes(@P("A question about a single Java topic, phrased like 'What is a record?'") String searchQuery) {
            IO.println("  [tool] searchJavaNotes(\"" + searchQuery + "\")");
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(EMBEDDING_MODEL.embed(searchQuery).content())
                    .maxResults(3)
                    .minScore(0.68)
                    .build();
            List<EmbeddingMatch<TextSegment>> matches = EMBEDDING_STORE.search(embeddingSearchRequest).matches();

            if (matches.isEmpty())
                return "No matching notes.";

            return matches
                    .stream()
                    .map(match -> match.embedded().text())
                    .collect(Collectors.joining("\n"));
        }
    }

    public interface MyChatBot {
        // Tells the model when to call the tool.
        @SystemMessage("""
                You answer questions about Java using only the Java notes, which you search with the searchJavaNotes tool.
                Search once per topic in the question, and search again with different words if the results miss.
                If the notes do not cover something, say so instead of answering from your own knowledge.""")
        ChatResponse sendUserMessage(UserMessage userMessage);
    }

    void main() {
        MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
                .chatModel(CHAT_MODEL)
                .chatMemory(CHAT_MEMORY)
                .tools(new JavaNoteTools())
                // Both handlers reverse the sync defaults (bad arguments fail the call; a tool's exception message goes to the model). Bad arguments: the error text goes back to the model so it can fix them and retry.
                .toolArgumentsErrorHandler(ToolArgumentsErrorHandler.sendExceptionMessageToLlm())
                // The tool throws: the call fails, unless the exception implements ToolErrorVisibleToLlm, in which case its messageForLlm() goes to the model instead.
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm())
                .build();

        String commandPrompt = "\nAsk about Java> ";
        for (String userInput = IO.readln(commandPrompt); !userInput.isEmpty(); userInput = IO.readln(commandPrompt)) {
            ChatResponse chatResponse = myChatBot.sendUserMessage(UserMessage.from(userInput));
            IO.println(chatResponse);
        }
    }

}
