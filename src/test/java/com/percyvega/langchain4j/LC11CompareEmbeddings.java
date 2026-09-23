package com.percyvega.langchain4j;

import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.percyvega.langchain4j.util.EmbeddingUtils.*;

@Log4j2
class LC11CompareEmbeddings {

    public static final String FILE_NAME = "/introduction-to-java.txt";

    public static final String COMMAND_PROMPT = "\nAsk me something about Java> ";

    private record Comparison(String sentence, float cosine, float euclidean) {
    }

    // Interactive, so it is a main() rather than a @Test: a test runner gives us no console to read from.
    void main() {
        log.info("Embedding and keeping sentences...");
        Map<String, Embedding> embeddingMap = EmbeddingUtils.getEmbeddings(FileUtils.getSentences(FILE_NAME));
        log.info("Sentences embedded and kept.");

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {

            float[] inputVector = getEmbedding(userInput).vector();

            log.info("Calculating relatedness to \"" + userInput + "\"");
            // compare userInput vector with each calculated vector
            List<Comparison> comparisons = embeddingMap.entrySet().stream()
                    .map(embeddingEntry -> new Comparison(embeddingEntry.getKey(),
                            cosineSimilarity(inputVector, embeddingEntry.getValue().vector()),
                            euclideanSimilarity(inputVector, embeddingEntry.getValue().vector())))
                    .sorted(Comparator.comparingDouble(Comparison::cosine).reversed())
                    .limit(3)
                    .toList();

            log.info("COSINE | EUCLIDEAN | SENTENCE");
            comparisons.forEach(comparison -> log.info(String.format("%6.4f |    %6.4f | %s",
                    comparison.cosine(), comparison.euclidean(), comparison.sentence())));
        }
    }

}
