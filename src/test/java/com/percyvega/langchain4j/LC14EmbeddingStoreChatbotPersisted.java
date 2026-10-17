package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

// The same chatbot, with the embedding store saved to a JSON file so only the first run pays OpenAI to embed the notes.
// Later runs load the file instead. Delete it after changing the notes or the embedding model, or the old vectors are reused.
class LC14EmbeddingStoreChatbotPersisted {

    private static final EmbeddingModel EMBEDDING_MODEL = ModelFactory.getOpenAiEmbeddingModel();
    private static final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);
    private static final String FILE_NAME = "/introduction-to-java.txt";
    public static final String EMBEDDING_STORE_FILE_NAME = "target/introduction-to-java-embedding-store.json";
    private static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    private static EmbeddingStore<TextSegment> EMBEDDING_STORE = new InMemoryEmbeddingStore<>();

    static {
        Path storeFile = Path.of(EMBEDDING_STORE_FILE_NAME);
        if (Files.exists(storeFile)) {
            EMBEDDING_STORE = InMemoryEmbeddingStore.fromFile(storeFile);
            IO.println("Reading from existing embedding store file: " + storeFile);
        } else {
            List<String> lines = FileUtils.getLines(FILE_NAME);
            EmbeddingUtils.getEmbeddings(EMBEDDING_MODEL, lines)
                    .forEach((line, embedding) -> EMBEDDING_STORE.add(embedding, TextSegment.from(line)));
            ((InMemoryEmbeddingStore<TextSegment>) EMBEDDING_STORE).serializeToFile(storeFile);
            IO.println("Embedding store has been created: " + storeFile);
        }
    }

    public interface MyChatBot {
        ChatResponse sendUserMessage(UserMessage userMessage);
    }

    void main() {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(EMBEDDING_MODEL)
                .embeddingStore(EMBEDDING_STORE)
                .maxResults(3)
                .build();
        MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
                .chatModel(ModelFactory.getGoogleChatModel())
                .chatMemory(CHAT_MEMORY)
                .contentRetriever(contentRetriever)
                .build();

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            ChatResponse chatResponse = myChatBot.sendUserMessage(UserMessage.from(userInput));
            IO.println(chatResponse.aiMessage().text());
        }
    }

}
