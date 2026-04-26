package com.percyvega;

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

    private static String fromKeychain(String service) {
        try {
            return new String(new ProcessBuilder("security", "find-generic-password",
                    "-a", System.getProperty("user.name"), "-s", service, "-w")
                    .start().getInputStream().readAllBytes()).trim();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + service + " from keychain", e);
        }
    }
}