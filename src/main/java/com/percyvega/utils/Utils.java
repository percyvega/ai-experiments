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
}
