package com.percyvega.langchain4j;

import com.percyvega.utils.ApiKeys;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModelName;

import java.time.Duration;

import static com.percyvega.utils.Constants.TIMEOUT_SECONDS;

public class EmbeddingModelFactory {

    public static EmbeddingModel getOpenAi() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(ApiKeys.openAI())
                .modelName(OpenAiEmbeddingModelName.TEXT_EMBEDDING_3_SMALL)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
    }

}
