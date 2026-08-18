package com.percyvega.langchain4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.List;

import static com.percyvega.utils.EmbeddingUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;

class T8Embedding {

    private static final Logger log = LogManager.getLogger(T8Embedding.class);

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    private record Comparison(String greeting, float cosine, float euclidean) {
    }

    @Test
    void logEmbedding() {
        Response<Embedding> response = EMBEDDING_MODEL.embed("My favorite food is the Peruvian Ceviche.");
        Embedding embedding = response.content();

        log.info("Embedding of size {}: {}", embedding.dimension(), embedding);
    }

    // Interactive, so it is a main() rather than a @Test: a test runner gives us no console to read from.
    void main() {
        List<String> greetings = readGreetings();
        String input = askForInput();

        // Embed the input once, then compare it against every greeting.
        float[] inputVector = getEmbeddingVector(EMBEDDING_MODEL, input);

        List<Comparison> comparisons = greetings.stream()
                .map(greeting -> {
                    float[] vector = getEmbeddingVector(EMBEDDING_MODEL, greeting);
                    return new Comparison(greeting,
                            cosineSimilarity(inputVector, vector),
                            euclideanSimilarity(inputVector, vector));
                })
                .sorted(Comparator.comparingDouble(Comparison::cosine).reversed())
                .toList();

        IO.println("Similarity to \"" + input + "\", from most to least similar:");
        IO.println("cosine | euclidean | greeting");
        comparisons.forEach(comparison -> IO.println(String.format("%6.4f |    %6.4f | %s",
                comparison.cosine(), comparison.euclidean(), comparison.greeting())));
    }

    private static List<String> readGreetings() {
        // One greeting per line, read from the test classpath.
        String greetingsFile = "/greetings.txt";

        try (InputStream inputStream = T8Embedding.class.getResourceAsStream(greetingsFile)) {
            if (inputStream == null) {
                throw new IllegalStateException("Not found on the classpath: " + greetingsFile);
            }

            return new BufferedReader(new InputStreamReader(inputStream, UTF_8))
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String askForInput() {
        // Used when there is no console to read from, so the run still produces a ranking.
        String defaultInput = "Hello!";

        // Returns null when there is no console, for instance under a test runner.
        String input = IO.readln("What is the most popular greeting in English? ");
        if (input == null || input.isBlank()) {
            IO.println("No input read, using \"" + defaultInput + "\" instead.");
            return defaultInput;
        }

        return input;
    }

}
