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

            Embedding inputEmbedding = EmbeddingUtils.getEmbedding(userInput);

            IO.println("Searching 3 most similar values to \"" + userInput + "\"");
            // Scores are (cosine + 1) / 2, so even unrelated text sits near 0.75.
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(inputEmbedding)
                    .maxResults(3) // default is 3
                    .minScore(0.75)
                    .build();
            EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStore.search(embeddingSearchRequest);

            IO.println("LANGCHAIN4J SCORE | QUESTION AND ANSWER");
            embeddingSearchResult.matches().forEach(embeddingMatch ->
                    IO.println(String.format("           %6.4f | %s", embeddingMatch.score(), embeddingMatch.embedded().text())));
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
