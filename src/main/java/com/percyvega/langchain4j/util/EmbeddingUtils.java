package com.percyvega.langchain4j.util;

import com.percyvega.langchain4j.factory.EmbeddingModelFactory;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class EmbeddingUtils {

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    private EmbeddingUtils() {
    }

    // One embedAll() call sends every sentence in a single request, rather than one request per sentence.
    // Duplicates are dropped first, so nothing is paid for twice.
    public static Map<String, Embedding> getEmbeddings(List<String> sentences) {
        IO.println("Embedding " + sentences.size() + " values...");
        List<String> distinctSentences = sentences.stream().distinct().toList();
        List<Embedding> vectors = EMBEDDING_MODEL.embedAll(distinctSentences.stream()
                .map(TextSegment::from)
                .toList()).content();

        LinkedHashMap<String, Embedding> embeddings = new LinkedHashMap<>();
        for (int i = 0; i < distinctSentences.size(); i++) {
            embeddings.put(distinctSentences.get(i), vectors.get(i));
        }
        IO.println("Embedded " + embeddings.size() + " values");
        return embeddings;
    }

    public static @NonNull Embedding getEmbedding(String input) {
        Response<Embedding> response = EMBEDDING_MODEL.embed(input);
        return response.content();
    }

    // Euclidean asks "are these vectors in the same place?"
    // Calculate the straight-line distance between the tips of two vectors (Pythagoras, in n dimensions),
    // then turn that (Euclidean) distance into a similarity score, so that bigger means more similar.
    // Euclidean similarity returns a range between:
    //  - 1 (distance 0, identical)
    //  - 0 (infinitely far apart, and never actually reached)
    // In practice it lands between 0.46 and 0.69, with 0.5 or less for unrelated vectors.
    public static float euclideanSimilarity(float[] vector1, float[] vector2) {
        // Calculate the Euclidean distance between two vectors.
        // Returns from 0 (identical) to +infinity (the farther apart, the less similar)
        float sumOfSquares = 0;
        for (int i = 0; i < vector1.length; i++) {
            float difference = vector1[i] - vector2[i];
            sumOfSquares += difference * difference;
        }
        float euclideanDistance = (float) Math.sqrt(sumOfSquares);

        return 1 / (1 + euclideanDistance);
    }

    // Cosine asks "are these vectors pointing the same way?"
    // Calculate the cosine of the angle between two vectors.
    // The angle between two vectors tells us how similar they are. A small angle means the vectors are pointing in a similar direction. They're more similar.
    // Cosine returns a range between:
    //  - +1 (0 degrees, same direction)
    //  -  0 (90 degrees, unrelated)
    //  - -1 (180 degrees, opposite meaning)
    // Embedding vectors are rarely opposite, so in practice it lands between 0.3 and 0.9, with 0.5 or less for unrelated vectors.
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
