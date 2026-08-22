package com.percyvega.utils;

import com.percyvega.langchain4j.EmbeddingModelFactory;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public abstract class EmbeddingUtils {

    private static final Logger log = LogManager.getLogger(EmbeddingUtils.class);

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    private EmbeddingUtils() {
    }

    public static Map<String, Embedding> getEmbeddings(List<String> sentences) {
        log.info("Embedding {} values...", sentences.size());
        LinkedHashMap<String, Embedding> embeddings = sentences.stream()
                .collect(toMap(s -> s,
                        EmbeddingUtils::getEmbedding,
                        (first, duplicate) -> first,
                        LinkedHashMap::new));
        log.info("Embedded {} values", embeddings.size());
        return embeddings;
    }

    public static @NonNull Embedding getEmbedding(String input) {
        Response<Embedding> response = EMBEDDING_MODEL.embed(input);
        return response.content();
    }

    // Calculate the Euclidean distance between two vectors.
    // Returns from 0 (identical) to +infinity (the farther apart, the less similar)
    private static float euclideanDistance(float[] vector1, float[] vector2) {
        float sumOfSquares = 0;
        for (int i = 0; i < vector1.length; i++) {
            float difference = vector1[i] - vector2[i];
            sumOfSquares += difference * difference;
        }

        return (float) Math.sqrt(sumOfSquares);
    }

    // Turn the Euclidean distance into a similarity score, so that bigger means more similar.
    // Returns from 1 (identical) to 0 (infinitely far apart).
    public static float euclideanSimilarity(float[] vector1, float[] vector2) {
        return 1 / (1 + euclideanDistance(vector1, vector2));
    }

    // Calculate the cosine of the angle between two vectors.
    // The angle between two vectors tells us how similar they are. A small angle means the vectors are pointing in a similar direction. They're more similar.
    // Returns +1 (0 degrees, same direction), 0 (90 degrees, unrelated) or -1 (180 degrees, opposite meaning).
    // Embedding vectors are rarely opposite, so in practice it lands between 0 and 1.
    public static float cosineSimilarity(float[] vector1, float[] vector2) {
        float dotProduct = 0;
        float norm1 = 0;
        float norm2 = 0;
        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            norm1 += vector1[i] * vector1[i];
            norm2 += vector2[i] * vector2[i];
        }

        if (norm1 == 0 || norm2 == 0) {
            throw new IllegalArgumentException("Cannot compute the cosine similarity of a zero vector");
        }

        return (float) (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2)));
    }

}
