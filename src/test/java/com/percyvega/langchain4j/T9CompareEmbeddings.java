package com.percyvega.langchain4j;

import dev.langchain4j.model.embedding.EmbeddingModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.percyvega.utils.EmbeddingUtils.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toMap;

class T9CompareEmbeddings {

    private static final Logger log = LogManager.getLogger(T9CompareEmbeddings.class);

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    private Map<String, float[]> greetingVectors;

    private record Comparison(String greeting, float cosine, float euclidean) {
    }

    // Interactive, so it is a main() rather than a @Test: a test runner gives us no console to read from.
    void main() {
        embedGreetings();

        String input = askForInput();
        float[] inputVector = getEmbedding(EMBEDDING_MODEL, input).vector();

        // compare input vector with each calculated vector
        List<Comparison> comparisons = greetingVectors.entrySet().stream()
                .map(greeting -> new Comparison(greeting.getKey(),
                        cosineSimilarity(inputVector, greeting.getValue()),
                        euclideanSimilarity(inputVector, greeting.getValue())))
                .sorted(Comparator.comparingDouble(Comparison::cosine).reversed())
                .toList();

        IO.println("Similarity to \"" + input + "\", from most to least similar:");
        IO.println("cosine | euclidean | greeting");
        comparisons.forEach(comparison -> IO.println(String.format("%6.4f |    %6.4f | %s",
                comparison.cosine(), comparison.euclidean(), comparison.greeting())));
    }

    // One call per greeting, so the log lines say when this starts and when it finishes.
    private void embedGreetings() {
        List<String> greetings = readGreetings();

        log.info("Embedding {} greetings...", greetings.size());
        // Collected into a LinkedHashMap, which keeps ties reading the way the file does.
        greetingVectors = greetings.stream()
                .collect(toMap(greeting -> greeting,
                        greeting -> getEmbedding(EMBEDDING_MODEL, greeting).vector(),
                        (first, duplicate) -> first,
                        LinkedHashMap::new));
        log.info("Embedded {} greetings", greetingVectors.size());
    }

    private static List<String> readGreetings() {
        // One greeting per line, read from the test classpath.
        String greetingsFile = "/greetings.txt";

        try (InputStream inputStream = T9CompareEmbeddings.class.getResourceAsStream(greetingsFile)) {
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
