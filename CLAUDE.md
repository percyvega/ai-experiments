# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A personal sandbox for calling LLM providers (OpenAI, Anthropic, Google, Ollama) from Java. Two parallel approaches live under `com.percyvega`:

- `raw` — direct HTTP via `java.net.http.HttpClient` + Jackson, no SDK.
- `langchain4j` — same providers via the [LangChain4j](https://docs.langchain4j.dev/) framework, extended into chat memory, AI Services, streaming, and embeddings.

There is no `Main`; experiments are driven from `src/test/java/...`. The `langchain4j` experiments are numbered `T1`…`T10` and build on each other in order — when adding one, continue the numbering and keep the "one new idea per class" shape.

## Build & run

- Java 25 toolchain (set via `maven.compiler.release=25` in `pom.xml`); requires `JAVA_HOME` pointing at a JDK 25. The interactive classes rely on Java 25 instance `main()` methods and the implicit `IO` class.
- `mvn compile` — build.
- `mvn test` — runs `RawTest`, `T1`, and `T2` only. Most `T*` classes are interactive `main()` methods, not tests; and `T8Embedding` *is* a `@Test` but its class name matches none of Surefire's default includes (`Test*`, `*Test`, `*Tests`, `*TestCase`), which the pom does not override — so it is skipped unless named via `-Dtest=T8Embedding` or run from the IDE.
- Parallel execution is currently **commented out** in `src/test/resources/junit-platform.properties`. If re-enabled, the four provider methods in each class fan out concurrently; the log4j2 pattern includes `[%t]` to tell them apart.

## Architecture

### `raw` package — template-method hierarchy

- `ModelHelper` (interface) — the public API surface: `getModelResponse(String)` (provider's raw JSON) and `extractPromptResponse(String)` (the assistant text pulled out of that JSON). Keep it slim.
- `AbstractModelHelper` — owns the shared HTTP plumbing (`HttpClient.send`, non-200 → `AuthenticationException`) and implements both interface methods as `final`. Declares three `protected abstract` template methods: `getHttpRequest(String)`, `getBody(String)`, and `getPromptResponsePath()`.
- `impl/*HelperImpl` — one `final class` per provider. Each has a `private` constructor and `public static final ModelHelper INSTANCE = new XHelperImpl();`. **`INSTANCE` is typed as `ModelHelper`, not the concrete class** — this is intentional so that `XHelperImpl.INSTANCE.` autocomplete only shows the interface methods. Don't widen the type.

Adding a new provider in `raw`: extend `AbstractModelHelper`, override the three `protected` methods, expose `INSTANCE` typed as `ModelHelper`. The body JSON shape differs per provider (compare `GoogleHelperImpl` `contents/parts` vs OpenAI/Anthropic/Ollama `messages`), as does the response pointer returned by `getPromptResponsePath()` (`/content/0/text`, `/candidates/0/content/parts/0/text`, `/choices/0/message/content`). Ollama is called through its OpenAI-compatible endpoint, so it shares OpenAI's shape.

### `langchain4j` package — factories

- `ChatModelFactory` — abstract, private constructor. One `public static ChatModel getX()` per provider (`getAnthropic`, `getOpenAi`, `getGoogle`, `getOllama`).
- `StreamingChatModelFactory` — same four methods, returning `StreamingChatModel`.
- `EmbeddingModelFactory` — `getOpenAi()` returning an `EmbeddingModel` (`text-embedding-3-small`).

Each builds a fresh model using the provider's builder, pulling every knob from `Constants`.

### Utilities (`com.percyvega.utils`)

- `Constants` — model names, `TEMPERATURE`, `MAX_TOKENS`, `TIMEOUT_SECONDS`, default system/user prompts, `COMMAND_PROMPT`. **This is the single place to change models or tuning**; both packages read from it. Note Anthropic gets `TEMPERATURE / 2` because its range is 0–1 while OpenAI/Google go to 2.
- `ApiKeys` — `openAI()`, `anthropic()`, `google()`.
- `JsonUtils` — `formatAsJson(String)` (Jackson pretty-print, used by `RawTest`) and `getValue(String, String)` (JSON-pointer extraction, used by `extractPromptResponse`).
- `FileUtils` — `getSentences(fileName)` reads a classpath resource and splits it with `BreakIterator` rather than on `"."`.
- `EmbeddingUtils` — `getEmbedding(String)` and `getEmbeddings(List<String>)`; the batch version dedupes and issues a single `embedAll` call, which matters for cost. Also `cosineSimilarity` and `euclideanSimilarity`, each with a comment giving the range it lands in — preserve those explanatory comments, they are the point of the class.

## Experiments

Two kinds, and the distinction matters when running them:

**JUnit `@Test` classes** — `raw/RawTest`, `T1UserMessageTest`, `T2SystemAndUserMessagesTest`, `T8Embedding`. Only the first three run under a bare `mvn test`; `T8Embedding` is skipped by Surefire's name filter (see Build & run).

**Interactive `main()` classes** — `T3Chatting`, `T4ChattingWithMemory`, `T5Chatbot`, `T6ChatbotWithAnnotations`, `T7ChatbotStreaming`, `T9CompareEmbeddings`, `T10EmbeddingStore`. They loop on `IO.readln(COMMAND_PROMPT)` until an empty line. They are `main()` rather than `@Test` deliberately: a test runner gives no console to read from. Don't convert them to tests.

What each one adds:

- `T1` — one string prompt per provider. `T2` — `SystemMessage` + `UserMessage` list.
- `T3` — prompt loop with no memory. `T4` — `MessageWindowChatMemory`, managed by hand.
- `T5` — the same via `AiServices` + a declared interface. `T6` — `@SystemMessage` / `@UserMessage` / `@V` templating.
- `T7` — `TokenStream` streaming with a `CompletableFuture` to await completion.
- `T8` — log a raw embedding vector. `T9` — hand-rolled cosine/euclidean retrieval over `src/test/resources/introduction-to-java.txt`. `T10` — the same via `InMemoryEmbeddingStore` and `EmbeddingSearchRequest` (whose scores are `(cosine + 1) / 2`, so unrelated text still sits near 0.75).

Logging in tests goes through `src/main/resources/log4j2.xml` (Maven puts main resources on the test classpath).

## API keys

- `com.percyvega.utils.ApiKeys` reads keys from the **macOS keychain** by shelling out to `security find-generic-password`.
- Service names: `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`, `GOOGLE_API_KEY`. This is macOS-only — there is no env-var or `.env` fallback.
- Ollama is local (`http://localhost:11434`) and needs no key.
- To add a new provider key, add a method to `ApiKeys` and store it in keychain via `security add-generic-password -a "$USER" -s NEW_KEY_NAME -w <key>`.
