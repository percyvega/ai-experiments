package com.percyvega.utils;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class ApiKeys {

    private ApiKeys() {}

    public static String openAI() {
        return fromKeychain("OPENAI_API_KEY");
    }

    public static String anthropic() {
        return fromKeychain("ANTHROPIC_API_KEY");
    }

    public static String google() {
        return fromKeychain("GOOGLE_API_KEY");
    }

    // e.g. $ security find-generic-password -a "$USER" -s ANTHROPIC_API_KEY
    private static String fromKeychain(String keyName) {
        try {
            return new String(new ProcessBuilder("security", "find-generic-password",
                    "-a", System.getProperty("user.name"), "-s", keyName, "-w")
                    .start().getInputStream().readAllBytes()).trim();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + keyName + " from keychain", e);
        }
    }
}