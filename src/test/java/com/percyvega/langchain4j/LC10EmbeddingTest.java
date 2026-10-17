package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

// What an embedding is: one sentence turned into a vector of numbers, printed raw.
class LC10EmbeddingTest {

    private static final EmbeddingModel EMBEDDING_MODEL = ModelFactory.getOpenAiEmbeddingModel();

    @Test
    void printEmbedding() {
        Response<Embedding> response = EMBEDDING_MODEL.embed("My favorite food is the Peruvian Ceviche.");
        IO.println(response);
    }

}
