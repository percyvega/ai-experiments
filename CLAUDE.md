# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A personal sandbox for calling LLM providers (OpenAI, Anthropic, Google, Ollama) from Java. Two parallel approaches live under `com.percyvega`:

- `plain` — direct HTTP via `java.net.http.HttpClient` + Jackson, no SDK.
- `langchain4j` — same providers via the [LangChain4j](https://docs.langchain4j.dev/) framework, extended into chat memory, AI Services, streaming, and embeddings.

There is no `Main`; experiments are driven from `src/test/java/...`. The `langchain4j` experiments are numbered `LC1`…`LC12` and build on each other in order — when adding one, continue the numbering and keep the "one new idea per class" shape.

## Build & run

- Java 25 toolchain (set via `maven.compiler.release=25` in `pom.xml`); requires `JAVA_HOME` pointing at a JDK 25. The interactive classes rely on Java 25 instance `main()` methods and the implicit `IO` class.
- `mvn compile` — build.
- `mvn test` — runs `P1GoogleTest`, `P2AllModelsRefactoredTest`, `LC1GoogleUserTextTest`, `LC2AllModelsUserTextTest`, `LC3UserMessageTest`, and `LC4SystemAndUserMessagesTest`. Most `LC*` classes are interactive `main()` methods, not tests; and `LC10Embedding` *is* a `@Test` but its class name matches none of Surefire's default includes (`Test*`, `*Test`, `*Tests`, `*TestCase`), which the pom does not override — so it is skipped unless named via `-Dtest=LC10Embedding` or run from the IDE.
- Parallel execution is enabled but **opt-in**: `src/test/resources/junit-platform.properties` switches the engine on yet leaves `mode.default` and `mode.classes.default` at `same_thread`, so only a class carrying `@Execution(ExecutionMode.CONCURRENT)` fans out. `LC2AllModelsUserTextTest`, `LC3UserMessageTest`, and `LC4SystemAndUserMessagesTest` carry it; `P1GoogleTest`, `P2AllModelsRefactoredTest`, and `LC1GoogleUserTextTest` do not. Pool size is fixed at 4, one per provider. All three print through `IO.println` and tag each line with the provider name themselves (`printResponse("Anthropic", ...)`), which is what keeps the interleaved output readable — the `[%t]` thread field in `log4j2.xml` does not apply to them, since they no longer log.
- Console output in the experiments goes through `IO.println`, not a logger — they are read as a transcript, so the level/timestamp/logger envelope was noise around a one-line answer. Logging proper is reserved for `src/main` (`EmbeddingUtils`, `FileUtils`) and the two embedding experiments that emit scored multi-line output (`LC11CompareEmbeddings`, `LC12EmbeddingStore`). Where a class does log, the logger comes from Lombok's `@Log4j2`, never a hand-written field. Since JDK 23 dropped implicit annotation processing, the pom lists Lombok under the compiler plugin's `<annotationProcessorPaths>` — a new Lombok annotation needs nothing further, but dropping that block silently breaks every `log` reference.
- The local Ollama server is started by hand, not by a script in this repo — `ollama serve` (backgrounded with `nohup ... &` so it survives the terminal closing), stopped with `pkill -f "ollama serve"`. Check it is up with `curl -s http://localhost:11434/`, and make sure the `MISTRAL_AI_MODEL_NAME` model from `Constants` is pulled (`ollama list`, else `ollama pull mistral-small3.2`).

## Architecture

### `plain` package — template-method hierarchy

- `ModelHelper` (interface) — the public API surface: `getModelResponse(String)` (provider's raw JSON) and `extractPromptResponse(String)` (the assistant text pulled out of that JSON). Keep it slim.
- `AbstractModelHelper` — owns the shared HTTP plumbing (`HttpClient.send`, non-200 → `RuntimeException` carrying the response body) and implements both interface methods as `final`. Declares three `protected abstract` template methods: `getHttpRequest(String)`, `getRequestPayload(String)`, and `getPromptResponseJsonPointer()`.
- `impl/*HelperImpl` — one `final class` per provider. Each has a `private` constructor and `public static final ModelHelper INSTANCE = new XHelperImpl();`. **`INSTANCE` is typed as `ModelHelper`, not the concrete class** — this is intentional so that `XHelperImpl.INSTANCE.` autocomplete only shows the interface methods. Don't widen the type.

Adding a new provider in `plain`: extend `AbstractModelHelper`, override the three `protected` methods, expose `INSTANCE` typed as `ModelHelper`. The body JSON shape differs per provider (compare `GoogleHelperImpl` `contents/parts` vs OpenAI/Anthropic/Ollama `messages`), as does the response pointer returned by `getPromptResponseJsonPointer()` (`/content/0/text`, `/candidates/0/content/parts/0/text`, `/choices/0/message/content`). Ollama is called through its OpenAI-compatible endpoint, so it shares OpenAI's shape.

### `langchain4j` package — factories

- `ChatModelFactory` — abstract, private constructor. One `public static ChatModel getX()` per provider (`getAnthropic`, `getOpenAi`, `getGoogle`, `getOllama`).
- `StreamingChatModelFactory` — same four methods, returning `StreamingChatModel`.
- `EmbeddingModelFactory` — `getOpenAi()` returning an `EmbeddingModel` (`text-embedding-3-small`).

Each builds a fresh model using the provider's builder, pulling every knob from `Constants`.

### Utilities — shared vs. per-approach

Helpers live beside the approach that uses them; only what both need stays in `com.percyvega.utils`.

**`com.percyvega.utils`** — shared by both packages:

- `Constants` — model names, `TEMPERATURE`, `MAX_TOKENS`, `TIMEOUT_SECONDS`, default system/user prompts, `COMMAND_PROMPT`. **This is the single place to change models or tuning**; both packages read from it. Note Anthropic gets `TEMPERATURE / 2` because its range is 0–1 while OpenAI/Google go to 2.
- `ApiKeys` — `openAI()`, `anthropic()`, `google()`.

**`com.percyvega.plain.util`** — only the plain-HTTP side:

- `JsonUtils` — `formatAsJson(String)` (Jackson pretty-print, used by `P1GoogleTest`) and `getValue(String, String)` (JSON-pointer extraction, used by `extractPromptResponse`). LangChain4j parses responses itself, so nothing outside `plain` needs this.

**`com.percyvega.langchain4j.util`** — only the framework side:

- `FileUtils` — `getSentences(fileName)` reads a classpath resource and splits it with `BreakIterator` rather than on `"."`.
- `EmbeddingUtils` — `getEmbedding(String)` and `getEmbeddings(List<String>)`; the batch version dedupes and issues a single `embedAll` call, which matters for cost. Also `cosineSimilarity` and `euclideanSimilarity`, each with a comment giving the range it lands in — preserve those explanatory comments, they are the point of the class.

## Experiments

Two kinds, and the distinction matters when running them:

**JUnit `@Test` classes** — `plain/P1GoogleTest`, `plain/P2AllModelsTest`, `LC1GoogleUserTextTest`, `LC2AllModelsUserTextTest`, `LC3UserMessageTest`, `LC4SystemAndUserMessagesTest`, `LC10Embedding`. All but the last run under a bare `mvn test`; `LC10Embedding` is skipped by Surefire's name filter (see Build & run).

**Interactive `main()` classes** — `LC5Chatting`, `LC6ChattingWithMemory`, `LC7Chatbot`, `LC8ChatbotWithAnnotations`, `LC9ChatbotStreaming`, `LC11CompareEmbeddings`, `LC12EmbeddingStore`. They loop on `IO.readln(COMMAND_PROMPT)` until an empty line. They are `main()` rather than `@Test` deliberately: a test runner gives no console to read from. Don't convert them to tests.

What each one adds:

- `LC1` — one provider (Google), model built inline rather than through `ChatModelFactory`; the framework mirror of `P1GoogleTest`.
- `LC2` — one string prompt per provider, via `ChatModelFactory`. `LC3` — the same prompt as a `UserMessage`. `LC4` — `SystemMessage` + `UserMessage` list.
- `LC5` — prompt loop with no memory. `LC6` — `MessageWindowChatMemory`, managed by hand.
- `LC7` — the same via `AiServices` + a declared interface. `LC8` — `@SystemMessage` / `@UserMessage` / `@V` templating.
- `LC9` — `TokenStream` streaming with a `CompletableFuture` to await completion.
- `LC10` — log a raw embedding vector. `LC11` — hand-rolled cosine/euclidean retrieval over `src/test/resources/introduction-to-java.txt` (150 Java Q&A pairs, one per line, each split by `BreakIterator` into a question chunk and an answer chunk). `LC12` — the same via `InMemoryEmbeddingStore` and `EmbeddingSearchRequest` (whose scores are `(cosine + 1) / 2`, so unrelated text still sits near 0.75).

What little logging the tests do — `LC11CompareEmbeddings` and `LC12EmbeddingStore` — is configured by `src/main/resources/log4j2.xml` (Maven puts main resources on the test classpath). Everything else prints with `IO.println` and is unaffected by that file.

## API keys

- `com.percyvega.utils.ApiKeys` reads keys from the **macOS keychain** by shelling out to `security find-generic-password`.
- Service names: `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`, `GOOGLE_API_KEY`. This is macOS-only — there is no env-var or `.env` fallback.
- Ollama is local (`http://localhost:11434`) and needs no key.
- To add a new provider key, add a method to `ApiKeys` and store it in keychain via `security add-generic-password -a "$USER" -s NEW_KEY_NAME -w <key>`.
