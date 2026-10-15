package com.percyvega.langchain4j;

import com.percyvega.langchain4j.factory.ModelFactory;
import com.percyvega.langchain4j.util.EmbeddingUtils;
import com.percyvega.langchain4j.util.FileUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.percyvega.langchain4j.util.EmbeddingUtils.cosineSimilarity;
import static com.percyvega.langchain4j.util.EmbeddingUtils.euclideanSimilarity;

// LC11 — hand-rolled retrieval: every line of a file is embedded, then ranked against your input
// by cosine and euclidean similarity. It stops at the matches.
class LC11EmbeddingAlgorithmsCompared {

    private static final EmbeddingModel EMBEDDING_MODEL = ModelFactory.getOpenAiEmbeddingModel();
    private static final String FILE_NAME = "/introduction-to-java.txt";
    private static final String COMMAND_PROMPT = "\nSemantic similarity search. Write something about Java> ";

    private record Comparison(String line, float cosine, float euclidean) {
    }

    private static final Map<String, Embedding> LINE_EMBEDDING_MAP = new HashMap<>();

    static {
        // A line is a question plus its answer, and that pair is the unit worth retrieving.
        List<String> lines = FileUtils.getLines(FILE_NAME);
        LINE_EMBEDDING_MAP.putAll(EmbeddingUtils.getEmbeddings(EMBEDDING_MODEL, lines));
    }

    void main() {
        for (String userInput = IO.readln(COMMAND_PROMPT); !userInput.isEmpty(); userInput = IO.readln(COMMAND_PROMPT)) {

            float[] inputVector = EMBEDDING_MODEL.embed(userInput).content().vector();

            IO.println("Searching the 3 most semantically similar values to \"" + userInput + "\"");
            // compare userInput vector with each calculated vector
            List<Comparison> comparisons = LINE_EMBEDDING_MAP.entrySet().stream()
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

}
