# ai-experiments

A personal sandbox for calling LLM providers from Java, in two flavors:

- **`raw`** — direct HTTP via `java.net.http.HttpClient` and Jackson, no SDK.
- **`langchain4j`** — the same providers wrapped through [LangChain4j](https://docs.langchain4j.dev/).

Currently supports:

- OpenAI (`gpt-4.1-mini`)
- Anthropic (Claude Sonnet 4.x — model IDs are pinned in `ChatModelFactory` and each `*HelperImpl`)
- Google (`gemini-2.5-flash`)
- Ollama (`qwen3:4b`, local)

## Requirements

- JDK 25 (`maven.compiler.release=25`)
- Maven 3.9+
- macOS (API keys are read from the macOS keychain — see below)
- [Ollama](https://ollama.com/) running locally on port `11434` if you want to call the local provider

## API keys

Keys are read from the macOS keychain via `security find-generic-password`. Store them once:

```sh
security add-generic-password -a "$USER" -s OPENAI_API_KEY    -w sk-...
security add-generic-password -a "$USER" -s ANTHROPIC_API_KEY -w sk-ant-...
security add-generic-password -a "$USER" -s GOOGLE_API_KEY    -w ...
```

Ollama needs no key.

## Run

There is no `Main` — experiments are driven by JUnit tests:

```sh
mvn test
```

Tests run **in parallel** (configured in `src/test/resources/junit-platform.properties`), so the four provider methods inside each test class fan out concurrently.

Test classes:

- `raw/RawTest` — exercises each `*HelperImpl.INSTANCE` (raw HTTP) and pretty-prints the JSON response.
- `langchain4j/UserMessageTest` — single-string prompt against each `ChatModelFactory.getX()`.
- `langchain4j/UserAndSystemMessagesTest` — `SystemMessage` + `UserMessage` list against each `ChatModel`.

Run a single class from your IDE, or:

```sh
mvn test -Dtest=UserMessageTest
```

## Architecture

### `raw` — template-method hierarchy

- `ModelHelper` — public interface, exposes only `getResponseFromPrompt(String)`.
- `AbstractModelHelper` — shared `HttpClient` + Jackson + `StopWatch` plumbing; declares `protected abstract getHttpRequest(...)` and `getBody(...)`.
- `impl/*HelperImpl` — one `final class` per provider, exposing `public static final ModelHelper INSTANCE`.

Adding a new provider: extend `AbstractModelHelper`, override the two `protected` methods, expose an `ModelHelper INSTANCE`.

### `langchain4j` — factory of `ChatModel`s

- `ChatModelFactory` — static `getAnthropic()` / `getOpenAi()` / `getGoogle()` / `getOllama()` returning a fresh LangChain4j `ChatModel`. Models, temperatures, and timeouts are hard-coded; tweak them here.
