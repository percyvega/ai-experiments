package com.percyvega.langchain4j;

import com.percyvega.utils.EmbeddingUtils;
import com.percyvega.utils.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

class T10EmbeddingStore {

    public static final String FILE_NAME = "/introduction-to-java.txt";
    private static final Logger log = LogManager.getLogger(T10EmbeddingStore.class);

    private static final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    public static final String COMMAND_PROMPT = "\nAsk me something about Java> ";

    void main() {
        embedAndStoreFileSentences();

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            log.info("Similarity to \"" + userInput + "\", from most to least similar:");

            Embedding questionEmbedding = EmbeddingUtils.getEmbedding(userInput);

            // Scores are (cosine + 1) / 2, so even unrelated text sits near 0.55. Hence, a floor well above it.
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(questionEmbedding)
                    .maxResults(3) // default is 3
                    .minScore(0.7)
                    .build();

            EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStore.search(embeddingSearchRequest);
            embeddingSearchResult.matches()
                    .forEach(match -> log.info("{} | {}", match.score(), match.embedded().text()));
        }
    }

    private static void embedAndStoreFileSentences() {
        log.info("Embedding and storing sentences...");
        List<String> sentences = FileUtils.getSentences(FILE_NAME);
        EmbeddingUtils.getEmbeddings(sentences)
                .forEach((key, embedding) -> {
                    TextSegment textSegment = TextSegment.from(key);
                    embeddingStore.add(embedding, textSegment);
                });
        log.info("Sentences embedded and stored.");
    }

}
