package com.percyvega.langchain4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
class LC9Embedding {

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    @Test
    void logEmbedding() {
        Response<Embedding> response = EMBEDDING_MODEL.embed("My favorite food is the Peruvian Ceviche.");
        log.info(response);
    }

}
