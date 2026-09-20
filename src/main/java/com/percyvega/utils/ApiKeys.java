package com.percyvega.utils;

import java.io.IOException;
import java.io.UncheckedIOException;

// Supplies each provider's API key, read from the macOS keychain — there is no env-var or .env fallback.
public final class ApiKeys {

    private ApiKeys() {
    }

    public static String openAI() {
        return fromKeychain("OPENAI_API_KEY");
    }

    public static String anthropic() {
        return fromKeychain("ANTHROPIC_API_KEY");
    }

    public static String google() {
        return fromKeychain("GOOGLE_API_KEY");
    }

    // Shells out to the macOS security tool to fetch the password stored under the given service name.
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