package com.percyvega.utils;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

public abstract class EmbeddingUtils {

    private EmbeddingUtils() {
    }

    public static float[] getEmbeddingVector(EmbeddingModel embeddingModel, String input) {
        Response<Embedding> response = embeddingModel.embed(input);
        return response.content().vector();
    }

    // Calculate the Euclidean distance between two vectors.
    // Returns from 0 (identical) to +infinity (the farther apart, the less similar)
    public static float euclideanDistance(float[] vector1, float[] vector2) {
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
