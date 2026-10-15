package com.percyvega.langchain4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class FileUtils {

    private static final Pattern WHITESPACES = Pattern.compile("\\s+");

    private FileUtils() {
    }

    // Gets all the sentences from a paragraph or a long line.
    // BreakIterator knows that a period is not always a sentence end, unlike splitting on "."
    private static List<String> getSentencesFromText(String text) {
        BreakIterator breakIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        breakIterator.setText(text);

        List<String> sentences = new ArrayList<>();
        int start = breakIterator.first();
        for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
            // Paragraphs are hard-wrapped, so a sentence carries the line breaks it spans.
            String sentence = WHITESPACES.matcher(text.substring(start, end)).replaceAll(" ").trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
        }

        return sentences;
    }

    private static String getContents(String fileName) {
        try (InputStream inputStream = FileUtils.class.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Not found on the classpath: " + fileName);
            }

            return new String(inputStream.readAllBytes(), UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<String> getSentences(String fileName) {
        return getSentencesFromText(getContents(fileName));
    }

    public static List<String> getLines(String fileName) {
        return getContents(fileName).lines()
                .map(line -> WHITESPACES.matcher(line).replaceAll(" ").trim())
                .filter(line -> !line.isEmpty())
                .toList();
    }

}
