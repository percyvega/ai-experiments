package com.percyvega.plain;

import com.percyvega.plain.impl.AnthropicHelperImpl;
import com.percyvega.plain.impl.GoogleHelperImpl;
import com.percyvega.plain.impl.OllamaHelperImpl;
import com.percyvega.plain.impl.OpenAiHelperImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    void main() {

        String prompt = "Tell me a single, short, and unpopular joke about programming";

        log.info("\n{}", OpenAiHelperImpl.INSTANCE.getResponseFromPrompt(prompt)); // 2 seconds
        log.info("\n{}", AnthropicHelperImpl.INSTANCE.getResponseFromPrompt(prompt)); // 5 seconds
        log.info("\n{}", GoogleHelperImpl.INSTANCE.getResponseFromPrompt(prompt)); // 18 seconds
        log.info("\n{}", OllamaHelperImpl.INSTANCE.getResponseFromPrompt(prompt)); // 82 seconds
    }

}
