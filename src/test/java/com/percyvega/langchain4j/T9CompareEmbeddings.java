package com.percyvega.langchain4j;

import com.percyvega.utils.EmbeddingUtils;
import com.percyvega.utils.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.percyvega.utils.EmbeddingUtils.*;

class T9CompareEmbeddings {

    public static final String FILE_NAME = "/introduction-to-java.txt";
    private static final Logger log = LogManager.getLogger(T9CompareEmbeddings.class);

    public static final String COMMAND_PROMPT = "\nAsk me something about Java> ";

    private record Comparison(String greeting, float cosine, float euclidean) {
    }

    // Interactive, so it is a main() rather than a @Test: a test runner gives us no console to read from.
    void main() {
        Map<String, Embedding> greetingVectors = EmbeddingUtils.getEmbeddings(FileUtils.getSentences(FILE_NAME));

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {

            float[] inputVector = getEmbedding(userInput).vector();

            log.info("Calculating relatedness to \"" + userInput + "\"");
            // compare userInput vector with each calculated vector
            List<Comparison> comparisons = greetingVectors.entrySet().stream()
                    .map(embeddingEntry -> new Comparison(embeddingEntry.getKey(),
                            cosineSimilarity(inputVector, embeddingEntry.getValue().vector()),
                            euclideanSimilarity(inputVector, embeddingEntry.getValue().vector())))
                    .sorted(Comparator.comparingDouble(Comparison::cosine).reversed())
                    .limit(8)
                    .toList();

            log.info("COSINE | EUCLIDEAN | SENTENCE");
            comparisons.forEach(comparison -> log.info(String.format("%6.4f |    %6.4f | %s",
                    comparison.cosine(), comparison.euclidean(), comparison.greeting())));
        }
    }

}
