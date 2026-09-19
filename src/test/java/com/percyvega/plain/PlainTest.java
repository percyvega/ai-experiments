package com.percyvega.raw;

import com.percyvega.raw.impl.AnthropicHelperImpl;
import com.percyvega.raw.impl.GoogleHelperImpl;
import com.percyvega.raw.impl.OllamaHelperImpl;
import com.percyvega.raw.impl.OpenAiHelperImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;
import static com.percyvega.utils.JsonUtils.formatAsJson;

class RawTest {

    @Test
    void anthropic() {
        logResponse(AnthropicHelperImpl.INSTANCE);
    }

    @Test
    void google() {
        logResponse(GoogleHelperImpl.INSTANCE);
    }

    @Test
    void openAi() {
        logResponse(OpenAiHelperImpl.INSTANCE);
    }

    @Test
    void ollama() {
        logResponse(OllamaHelperImpl.INSTANCE);
    }

    private void logResponse(ModelHelper modelHelper) {
        IO.println(formatAsJson(modelHelper.getModelResponse(USER_MESSAGE_TEXT)));
//        log.info("\n{}", modelHelper.extractPromptResponse(modelHelper.getModelResponse(USER_MESSAGE_TEXT)));
    }
}
