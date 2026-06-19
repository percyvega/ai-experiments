# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A personal sandbox for calling LLM providers (OpenAI, Anthropic, Google, Ollama) from Java. Two parallel approaches live under `com.percyvega`:

- `raw` — direct HTTP via `java.net.http.HttpClient` + Jackson, no SDK.
- `langchain4j` — same providers via the [LangChain4j](https://docs.langchain4j.dev/) framework.

There is no `Main`; experiments are driven by JUnit tests under `src/test/java/...`.

## Build & run

- Java 25 toolchain (set via `maven.compiler.release=25` in `pom.xml`); requires `JAVA_HOME` pointing at a JDK 25.
- `mvn compile` — build.
- `mvn test` — runs all tests. Tests are configured to execute **in parallel** (see `src/test/resources/junit-platform.properties`), so the four provider methods in each class fan out concurrently.

## Architecture

### `raw` package — template-method hierarchy

- `AiHelper` (interface) — the **only** public API surface: `String getResponseFromPrompt(String)`. Keep it slim; do not add methods here that callers shouldn't see.
- `AbstractAiHelper` — owns the shared HTTP plumbing (`HttpClient.send`, `StopWatch` timing) and declares two `protected abstract` template methods: `getHttpRequest(String)` and `getBody(String)`. `getResponseFromPrompt` is `final` here.
- `impl/*HelperImpl` — one `final class` per provider. Each has a `private` constructor and `public static final AiHelper INSTANCE = new XHelperImpl();`. **`INSTANCE` is typed as `AiHelper`, not the concrete class** — this is intentional so that `XHelperImpl.INSTANCE.` autocomplete only shows `getResponseFromPrompt`. Don't widen the type.

Adding a new provider in `raw`: extend `AbstractAiHelper`, override the two `protected` methods, expose `INSTANCE` typed as `AiHelper`. The body JSON shape differs per provider (compare `GoogleHelperImpl` `contents/parts` vs OpenAI/Anthropic/Ollama `messages`).

### `langchain4j` package — factory of `ChatModel`s

- `ChatModelFactory` — abstract, package-private constructor. Exposes one `public static ChatModel getX()` per provider (`getAnthropic`, `getOpenAi`, `getGoogle`, `getOllama`). Each builds a fresh LangChain4j `ChatModel` using the provider's builder. Models, temperatures, and timeouts are hard-coded — change them here.

### Utilities

- `com.percyvega.utils.Utils.formatAsJson(String)` — Jackson pretty-print used by `RawTest` to render raw HTTP responses.

## Tests

- `raw/RawTest` — calls each `*HelperImpl.INSTANCE` and pretty-prints the JSON.
- `langchain4j/UserMessageTest` — single-string prompt against each `ChatModelFactory.getX()`.
- `langchain4j/UserAndSystemMessagesTest` — `SystemMessage` + `UserMessage` list against each model.

Logging in tests goes through `src/main/resources/log4j2.xml` (Maven puts main resources on the test classpath). The pattern includes `[%t]`, useful for distinguishing parallel-test output.

## API keys

- `com.percyvega.utils.ApiKeys` reads keys from the **macOS keychain** by shelling out to `security find-generic-password`.
- Service names: `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`, `GOOGLE_API_KEY`. This is macOS-only — there is no env-var or `.env` fallback.
- Ollama is local (`http://localhost:11434`) and needs no key.
- To add a new provider key, add a method to `ApiKeys` and store it in keychain via `security add-generic-password -a "$USER" -s NEW_KEY_NAME -w <key>`.
