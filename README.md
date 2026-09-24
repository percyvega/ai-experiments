# ai-experiments

A personal sandbox for calling LLM providers from Java, in two flavors:

- **`plain`** — direct HTTP via `java.net.http.HttpClient` and Jackson, no SDK. Shows what the providers' wire formats actually look like.
- **`langchain4j`** — the same providers through [LangChain4j](https://docs.langchain4j.dev/), then further into memory, AI Services, streaming, and embeddings.

The `langchain4j` experiments are numbered `LC1` … `LC12` and are meant to be read in order — each one adds a single idea to the one before it.

## Providers

| Provider  | Chat model              | Notes                                 |
|-----------|-------------------------|---------------------------------------|
| Anthropic | `CLAUDE_SONNET_4_6`     | temperature halved (its range is 0–1) |
| OpenAI    | `GPT_4_1`               | also the embedding provider           |
| Google    | `gemini-2.5-flash-lite` |                                       |
| Ollama    | `mistral-small3.2`      | local, no API key                     |

Embeddings use OpenAI `text-embedding-3-small`.

Model names, temperature, max tokens, timeout, and the default prompts all live in one place: **`com.percyvega.utils.Constants`**. Change them there and both packages follow.

## Requirements

- JDK 25 (`maven.compiler.release=25`). The interactive experiments use Java 25 instance `main()` methods and the implicit `IO` class, so an older JDK will not compile them.
- Maven 3.9+
- macOS — API keys are read from the macOS keychain (see below)
- [Ollama](https://ollama.com/) on port `11434` if you want the local provider — start it with `ollama serve` and pull the model named by `MISTRAL_AI_MODEL_NAME` in `Constants`
- Lombok supplies the `@Log4j2` loggers. JDK 23 dropped implicit annotation processing, so the pom names Lombok in `<annotationProcessorPaths>`; in an IDE, make sure annotation processing is enabled

## API keys

Keys are read from the macOS keychain via `security find-generic-password`. There is no env-var or `.env` fallback. Store them once:

```sh
security add-generic-password -a "$USER" -s OPENAI_API_KEY    -w sk-...
security add-generic-password -a "$USER" -s ANTHROPIC_API_KEY -w sk-ant-...
security add-generic-password -a "$USER" -s GOOGLE_API_KEY    -w ...
```

Ollama needs no key.

## Running

There is no `Main`. Every experiment lives under `src/test/java`, and there are two kinds:

**JUnit tests** — run with Maven or from the IDE:

```sh
mvn test                        # P1, P2, LC1, LC2, LC3, LC4 — every class whose name ends in Test
mvn test -Dtest=LC3UserMessageTest
mvn test -Dtest=LC10Embedding   # LC10 needs naming explicitly; see below
```

One wrinkle: Surefire only picks up classes matching `Test*` / `*Test` / `*Tests` / `*TestCase`, and the pom does not override that. `LC10EmbeddingTest` is a real `@Test` but its name matches none of those, so a bare `mvn test` **silently skips it**. Run it from the IDE, name it with `-Dtest=`, or rename the class if you want it in the default run.

**Interactive `main()` methods** — these read from the console, which a test runner does not give you, so run them from the IDE (green gutter arrow) rather than through `mvn test`. Enter an empty line to quit.

| Experiment                   | Kind        | What it shows                                                                                   |
|------------------------------|-------------|-------------------------------------------------------------------------------------------------|
| `P1GoogleTest`               | JUnit       | One provider, no abstractions: a hand-built `HttpClient` call to Google, printing the raw body    |
| `P2AllModelsRefactoredTest`            | JUnit       | Each `*HelperImpl.INSTANCE` over plain HTTP; pretty-prints the raw JSON response                  |
| `LC1GoogleUserTextTest`              | JUnit       | The same first contact through LangChain4j: a `GoogleAiGeminiChatModel` built inline              |
| `LC2AllModelsUserTextTest`     | JUnit       | The smallest thing that works: one string prompt to each `ChatModel`, via `ChatModelFactory`      |
| `LC3UserMessageTest`         | JUnit       | The same prompt wrapped in a `UserMessage` instead of passed as a bare string                    |
| `LC4SystemAndUserMessagesTest`| JUnit       | A `SystemMessage` + `UserMessage` list instead of a bare string                                  |
| `LC5Chatting`                | Interactive | A prompt loop — and the demonstration that, with no memory, the model forgets every turn         |
| `LC6ChattingWithMemory`       | Interactive | `MessageWindowChatMemory` (10 messages), fed and updated by hand                                 |
| `LC7Chatbot`                  | Interactive | The same thing via `AiServices` — declare an interface, let LangChain4j wire the memory          |
| `LC8ChatbotWithAnnotations`   | Interactive | `@SystemMessage` / `@UserMessage` / `@V` prompt templating on the interface                      |
| `LC9ChatbotStreaming`         | Interactive | `StreamingChatModel` + `TokenStream`, printing partial responses as they arrive                  |
| `LC10EmbeddingTest`               | JUnit\*     | What an embedding *is* — log the raw vector for one sentence                                     |
| `LC11CompareEmbeddings`       | Interactive | Hand-rolled retrieval: cosine vs. euclidean similarity over the sentences of a text file          |
| `LC12EmbeddingStore`         | Interactive | The same retrieval, but with LangChain4j's `InMemoryEmbeddingStore` and its scoring               |

\* `LC10` is a `@Test`, but not one `mvn test` finds on its own — see the naming note above.

`LC11` and `LC12` both embed `src/test/resources/introduction-to-java.txt` at startup, then let you ask questions against it and show the closest matches. That file is 150 short Java Q&A pairs, one per line; `FileUtils.getSentences` splits on sentence boundaries, so each question and its answer become separate chunks — which is why a typed question tends to match a stored question almost exactly.

Parallel execution is enabled but **opt-in**. `src/test/resources/junit-platform.properties` turns the engine on while leaving both default modes at `same_thread`, so only a class annotated `@Execution(ExecutionMode.CONCURRENT)` fans out — currently `LC2AllModelsUserTextTest`, `LC3UserMessageTest`, and `LC4SystemAndUserMessagesTest`, whose four provider methods run across a fixed pool of 4. `P1GoogleTest`, `P2AllModelsRefactoredTest`, and `LC1GoogleUserTextTest` stay sequential. The log pattern includes `[%t]` so you can tell the threads apart.

## Architecture

### `plain` — template-method hierarchy

- `ModelHelper` — the public interface: `getModelResponse(String)` returns the provider's raw JSON, `extractPromptResponse(String)` digs the assistant's text back out of it.
- `AbstractModelHelper` — owns the shared `HttpClient` plumbing and the non-200 handling; both interface methods are `final` here. Subclasses fill in three `protected abstract` hooks: `getHttpRequest(String)`, `getRequestPayload(String)`, and `getPromptResponseJsonPointer()`.
- `impl/*HelperImpl` — one `final class` per provider, with a private constructor and a `public static final ModelHelper INSTANCE`. `INSTANCE` is deliberately typed as the interface, not the concrete class, so autocomplete on it stays small.

The interesting part is the diff between providers: Google nests `contents`/`parts` where the others use `messages`, and each one buries the reply at a different JSON pointer (`/content/0/text` for Anthropic, `/candidates/0/content/parts/0/text` for Google, `/choices/0/message/content` for OpenAI and Ollama — Ollama being served through its OpenAI-compatible endpoint).

Adding a provider: extend `AbstractModelHelper`, implement the three `protected` methods, expose a `ModelHelper INSTANCE`.

### `langchain4j` — factories

- `ChatModelFactory` — `getAnthropic()` / `getOpenAi()` / `getGoogle()` / `getOllama()`, each returning a fresh `ChatModel`.
- `StreamingChatModelFactory` — the same four, returning `StreamingChatModel` (used by `LC9`).
- `EmbeddingModelFactory` — `getOpenAi()`, returning an `EmbeddingModel`.

### Utilities

Each helper sits next to the approach that uses it; `com.percyvega.utils` holds only what both need.

`com.percyvega.utils` — shared:

- `Constants` — model names, temperature, max tokens, timeout, default system/user prompts.
- `ApiKeys` — keychain lookups (`openAI()`, `anthropic()`, `google()`).

`com.percyvega.plain.util` — plain HTTP only:

- `JsonUtils` — `formatAsJson` (pretty-print) and `getValue` (JSON-pointer extraction). LangChain4j does its own parsing, so only `plain` needs this.

`com.percyvega.langchain4j.util` — framework side only:

- `FileUtils` — `getSentences(fileName)`, splitting a classpath resource into sentences with `BreakIterator` rather than on `"."`.
- `EmbeddingUtils` — `getEmbedding` / `getEmbeddings` (one batched `embedAll` call, duplicates dropped first) plus `cosineSimilarity` and `euclideanSimilarity`, each documented with the range it actually lands in.
