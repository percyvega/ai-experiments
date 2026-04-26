package com.percyvega.plain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.percyvega.plain.AiProvider.*;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    void main() {

        String prompt = "Tell me a joke about programming";

        log.info(OPENAI.getResponseFromPrompt(prompt));
        log.info(ANTHROPIC.getResponseFromPrompt(prompt));
        log.info(GOOGLE.getResponseFromPrompt(prompt));
    }

}
