package com.percyvega.langchain4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

class LC10Embedding {

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    @Test
    void printEmbedding() {
        Response<Embedding> response = EMBEDDING_MODEL.embed("My favorite food is the Peruvian Ceviche.");
        IO.println(response);
    }

}
