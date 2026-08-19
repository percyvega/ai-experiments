package com.percyvega.langchain4j;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

class T10EmbeddingStore {

    private static final Logger log = LogManager.getLogger(T10EmbeddingStore.class);

    private static final EmbeddingModel EMBEDDING_MODEL = EmbeddingModelFactory.getOpenAi();

    void main() {
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        getFileLines().stream()
                .flatMap(line -> getSentences(line).stream())
                .forEach(sentence -> {
                    TextSegment textSegment = TextSegment.from(sentence);
                    Embedding embedding = EMBEDDING_MODEL.embed(textSegment).content();
                    embeddingStore.add(embedding, textSegment);
                });
        log.info("File read; sentences embedded and stored.");

        // A search needs something to compare against, so the question gets embedded too.
        Embedding questionEmbedding = EMBEDDING_MODEL.embed("How does Java run on different operating systems?").content();

        // Scores are (cosine + 1) / 2, so even unrelated text sits near 0.55. Hence a floor well above it.
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(3) // default is 3
                .minScore(0.7)
                .build();

        EmbeddingSearchResult<TextSegment> embeddingSearchResult = embeddingStore.search(embeddingSearchRequest);
        embeddingSearchResult.matches()
                .forEach(match -> log.info("{} | {}", match.score(), match.embedded().text()));

    }

    // BreakIterator knows that a period is not always a sentence end, unlike splitting on ".".
    private static List<String> getSentences(String text) {
        BreakIterator breakIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        breakIterator.setText(text);

        List<String> sentences = new ArrayList<>();
        int start = breakIterator.first();
        for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
            String sentence = text.substring(start, end).trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
        }

        return sentences;
    }

    private static List<String> getFileLines() {
        String fileName = "/introduction-to-java.txt";

        try (InputStream inputStream = T10EmbeddingStore.class.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Not found on the classpath: " + fileName);
            }

            // Blank lines separate the paragraphs, so dropping them leaves one paragraph per line.
            return new BufferedReader(new InputStreamReader(inputStream, UTF_8))
                    .lines()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
