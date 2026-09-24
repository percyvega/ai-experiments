package com.percyvega.langchain4j;

import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

class LC12EmbeddingStore {

    public static final String FILE_NAME = "/introduction-to-java.txt";

    private static final EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    public static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    void main() {
        embedAndStoreFileLines();

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {
            IO.println("Similarity to \"" + userInput + "\", from most to least similar:");

            Embedding questionEmbedding = EmbeddingUtils.getEmbedding(userInput);

            // Scores are (cosine + 1) / 2, so even unrelated text sits near 0.75.
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(questionEmbedding)
                    .maxResults(3) // default is 3
                    .minScore(0.75)
                    .build();

            EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStore.search(embeddingSearchRequest);
            IO.println("LANGCHAIN4J SCORE | QUESTION AND ANSWER");
            embeddingSearchResult.matches()
                    .forEach(match -> IO.println(String.format("           %6.4f | %s", match.score(), match.embedded().text())));
        }
    }

    private static void embedAndStoreFileLines() {
        IO.println("Embedding and storing lines...");
        List<String> lines = FileUtils.getLines(FILE_NAME);
        EmbeddingUtils.getEmbeddings(lines)
                .forEach((key, embedding) -> {
                    TextSegment textSegment = TextSegment.from(key);
                    embeddingStore.add(embedding, textSegment);
                });
        IO.println("Lines embedded and stored.");
    }

}
