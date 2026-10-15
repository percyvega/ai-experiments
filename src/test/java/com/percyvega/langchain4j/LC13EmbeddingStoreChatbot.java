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

import java.util.List;

// LC13 — LangChain4j searches with your exact words on every message, before the model sees it, and appends the matches to the prompt.
// The default template says "Answer using the following information", not "only", so the model freely adds what it already knows.
class LC13EmbeddingStoreChatbot {

    private static final EmbeddingModel EMBEDDING_MODEL = ModelFactory.getOpenAiEmbeddingModel();
    private static final ChatMemory CHAT_MEMORY = MessageWindowChatMemory.withMaxMessages(10);
    private static final String FILE_NAME = "/introduction-to-java.txt";
    private static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    private static final EmbeddingStore<TextSegment> EMBEDDING_STORE = new InMemoryEmbeddingStore<>();

    static {
        // A line is a question plus its answer, and that pair is the unit worth retrieving.
        List<String> lines = FileUtils.getLines(FILE_NAME);
        EmbeddingUtils.getEmbeddings(EMBEDDING_MODEL, lines)
                .forEach((line, embedding) -> EMBEDDING_STORE.add(embedding, TextSegment.from(line)));
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
