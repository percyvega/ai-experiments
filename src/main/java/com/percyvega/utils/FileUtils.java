package com.percyvega.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class FileUtils {

    private static final Logger log = LogManager.getLogger(FileUtils.class);

    // BreakIterator knows that a period is not always a sentence end, unlike splitting on ".".
    private static List<String> getSentencesFromText(String text) {
        BreakIterator breakIterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
        breakIterator.setText(text);

        List<String> sentences = new ArrayList<>();
        int start = breakIterator.first();
        for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end, end = breakIterator.next()) {
            // Paragraphs are hard-wrapped, so a sentence carries the line breaks it spans.
            String sentence = text.substring(start, end).replaceAll("\\s+", " ").trim();
            if (!sentence.isEmpty()) {
                sentences.add(sentence);
            }
        }

//        log.info("Sentences:");
//        sentences.forEach(s -> IO.println(s));
        return sentences;
    }

    private static String getContents(String fileName) {
        try (InputStream inputStream = FileUtils.class.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Not found on the classpath: " + fileName);
            }

            String contents = new String(inputStream.readAllBytes(), UTF_8);
//            log.info("File contents: \n" + contents);
            return contents;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static List<String> getSentences(String fileName) {
        return getSentencesFromText(getContents(fileName));
    }

}
