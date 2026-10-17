package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;

// The same retrieval, with LangChain4j's InMemoryEmbeddingStore doing the search and the scoring.
// It stops at the matches.
class LC12EmbeddingStore {

    private static final EmbeddingModel EMBEDDING_MODEL = ModelFactory.getOpenAiEmbeddingModel();
    private static final String FILE_NAME = "/introduction-to-java.txt";
    private static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    private static final EmbeddingStore<TextSegment> EMBEDDING_STORE = new InMemoryEmbeddingStore<>();

    static {
        List<String> lines = FileUtils.getLines(FILE_NAME);
        EmbeddingUtils.getEmbeddings(EMBEDDING_MODEL, lines)
                .forEach((line, embedding) -> EMBEDDING_STORE.add(embedding, TextSegment.from(line)));
    }

    void main() {
        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {

            Embedding inputEmbedding = EMBEDDING_MODEL.embed(userInput).content();

            IO.println("Searching the 3 most semantically similar values to \"" + userInput + "\"");
            // Scores are (cosine + 1) / 2, so even unrelated text sits near 0.68
            EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(inputEmbedding)
                    .maxResults(3) // default is 3
                    .minScore(0.68)
                    .build();
            EmbeddingSearchResult<TextSegment> embeddingSearchResult = EMBEDDING_STORE.search(embeddingSearchRequest);

            IO.println("LANGCHAIN4J SCORE | QUESTION AND ANSWER");
            embeddingSearchResult.matches().forEach(embeddingMatch ->
                    IO.println(String.format("           %6.4f | %s", embeddingMatch.score(), embeddingMatch.embedded().text())));
        }
    }

}
