package com.percyvega.langchain4j;

import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.percyvega.langchain4j.util.EmbeddingUtils.cosineSimilarity;
import static com.percyvega.langchain4j.util.EmbeddingUtils.euclideanSimilarity;

class LC11EmbeddingAlgorithmsCompared {

    public static final String FILE_NAME = "/introduction-to-java.txt";

    private static final Map<String, Embedding> lineEmbeddingMap = new HashMap<>();
    public static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    private record Comparison(String line, float cosine, float euclidean) {
    }

    void main() {
        embedAndStoreFileLines();

        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {

            float[] inputVector = EmbeddingUtils.getEmbedding(userInput).vector();

            IO.println("Searching 3 most similar values to \"" + userInput + "\"");
            // compare userInput vector with each calculated vector
            List<Comparison> comparisons = lineEmbeddingMap.entrySet().stream()
                    .map(e -> {
                        String k = e.getKey();
                        Embedding v = e.getValue();
                        float[] vector = v.vector();
                        return new Comparison(k, cosineSimilarity(inputVector, vector), euclideanSimilarity(inputVector, vector));
                    })
                    .sorted(Comparator.comparingDouble(Comparison::cosine).reversed())
                    .limit(3)
                    .toList();

            IO.println("COSINE | EUCLIDEAN | QUESTION AND ANSWER");
            comparisons.forEach(c ->
                    IO.println(String.format("%6.4f |    %6.4f | %s", c.cosine(), c.euclidean(), c.line())));
        }
    }

    private static void embedAndStoreFileLines() {
        // A line is a question plus its answer, and that pair is the unit worth retrieving.
        List<String> lines = FileUtils.getLines(FILE_NAME);
        lineEmbeddingMap.putAll(EmbeddingUtils.getEmbeddings(lines));
    }

}
