package com.percyvega.langchain4j;

import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
class LC11EmbeddingStore {

    public static final String FILE_NAME = "/introduction-to-java.txt";

    private static final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    public static final String COMMAND_PROMPT = "\nAsk me something about Java> ";

    void main() {
        embedAndStoreFileSentences();

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            log.info("Similarity to \"" + userInput + "\", from most to least similar:");

            Embedding questionEmbedding = EmbeddingUtils.getEmbedding(userInput);

            // Scores are (cosine + 1) / 2, so even unrelated text sits near 0.75.
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(questionEmbedding)
                    .maxResults(3) // default is 3
                    .minScore(0.75)
                    .build();

            EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStore.search(embeddingSearchRequest);
            log.info("LANGCHAIN4J SCORE | SENTENCE");
            embeddingSearchResult.matches()
                    .forEach(match -> log.info(String.format("           %6.4f | %s", match.score(), match.embedded().text())));
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
