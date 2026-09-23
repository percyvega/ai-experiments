package com.percyvega.plain;

import com.percyvega.plain.impl.AnthropicHelperImpl;
import com.percyvega.plain.impl.GoogleHelperImpl;
import com.percyvega.plain.impl.OllamaHelperImpl;
import com.percyvega.plain.impl.OpenAiHelperImpl;
import com.percyvega.plain.util.JsonUtils;
import org.junit.jupiter.api.Test;

import static com.percyvega.utils.Constants.USER_MESSAGE_TEXT;

class P2AllModelsTest {

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
        String modelResponse = modelHelper.getModelResponse(USER_MESSAGE_TEXT);
        IO.println(JsonUtils.formatAsJson(modelResponse));
        //IO.println(modelHelper.extractPromptResponse(modelResponse));
    }
}
