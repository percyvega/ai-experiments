package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.L10EmbeddingModelFactory;
import com.percyvega.langchain4j.factory.L2ChatModelFactory;
import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

class LC13EmbeddingStoreContentRetriever {

    private static final EmbeddingModel EMBEDDING_MODEL = L10EmbeddingModelFactory.getOpenAi();

    public static final String FILE_NAME = "/introduction-to-java.txt";

    private static final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    public static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    public interface MyChatBot {
        ChatResponse sendUserMessage(UserMessage userMessage);
    }

    void main() {
        embedAndStoreFileLines();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(EMBEDDING_MODEL)
                .embeddingStore(embeddingStore)
                .maxResults(3)
                .build();
        MyChatBot myChatBot = AiServices.builder(MyChatBot.class)
                .chatModel(L2ChatModelFactory.getGoogle())
                .contentRetriever(contentRetriever)
                .build();

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            ChatResponse chatResponse = myChatBot.sendUserMessage(UserMessage.from(userInput));
            IO.println(chatResponse.aiMessage().text());
        }
    }

    private static void embedAndStoreFileLines() {
        // A line is a question plus its answer, and that pair is the unit worth retrieving.
        List<String> lines = FileUtils.getLines(FILE_NAME);
        EmbeddingUtils.getEmbeddings(lines)
                .forEach((k, v) -> {
                    TextSegment textSegment = TextSegment.from(k);
                    embeddingStore.add(v, textSegment);
                });
    }

}
