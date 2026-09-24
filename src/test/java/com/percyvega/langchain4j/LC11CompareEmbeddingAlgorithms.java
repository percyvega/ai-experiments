package com.percyvega.langchain4j;

import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.percyvega.langchain4j.util.EmbeddingUtils.*;

class LC11CompareEmbeddingAlgorithms {

    public static final String FILE_NAME = "/introduction-to-java.txt";

    public static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    private record Comparison(String line, float cosine, float euclidean) {
    }

    void main() {
        // A line is a question plus its answer, and that pair is the unit worth retrieving.
        IO.println("Embedding and keeping lines...");
        Map<String, Embedding> embeddingMap = EmbeddingUtils.getEmbeddings(FileUtils.getLines(FILE_NAME));
        IO.println("Lines embedded and kept.");

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {

            float[] inputVector = getEmbedding(userInput).vector();

            IO.println("Calculating relatedness to \"" + userInput + "\"");
            // compare userInput vector with each calculated vector
            List<Comparison> comparisons = embeddingMap.entrySet().stream()
                    .map(embeddingEntry -> new Comparison(embeddingEntry.getKey(),
                            cosineSimilarity(inputVector, embeddingEntry.getValue().vector()),
                            euclideanSimilarity(inputVector, embeddingEntry.getValue().vector())))
                    .sorted(Comparator.comparingDouble(Comparison::cosine).reversed())
                    .limit(3)
                    .toList();

            IO.println("COSINE | EUCLIDEAN | QUESTION AND ANSWER");
            comparisons.forEach(comparison -> IO.println(String.format("%6.4f |    %6.4f | %s",
                    comparison.cosine(), comparison.euclidean(), comparison.line())));
        }
    }

}
