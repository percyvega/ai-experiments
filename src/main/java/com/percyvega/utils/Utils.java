package com.percyvega.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Utils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Utils() {
    }

    public static String formatAsJson(String response) {
        try {
            return MAPPER.readTree(response).toPrettyString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getValue(String json, String jsonPointer) {
        try {
            return MAPPER.readTree(json).at(jsonPointer).asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
