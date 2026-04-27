# ai-experiments

A personal sandbox for calling LLM provider HTTP APIs directly from Java — no SDKs, just `java.net.http.HttpClient` and Jackson.

Currently supports:

- OpenAI (`gpt-4`)
- Anthropic (`claude-sonnet-4-6`)
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

`Main` uses the JEP 512 instance `void main()` form. Pick a provider by uncommenting a line in [`Main.java`](src/main/java/com/percyvega/plain/Main.java), then run from your IDE, or:

```sh
mvn compile
mvn exec:java -Dexec.mainClass=com.percyvega.plain.Main
```

Example (Ollama):

```
2026-04-26 21:41:23.893 [main] INFO  Status: 200
2026-04-26 21:41:45.... [main] INFO  Time: 22 sec
2026-04-26 21:41:45.... [main] INFO
{
  "id" : "...",
  "choices" : [ { "message" : { "content" : "..." } } ]
}
```

## Architecture

The `plain` package is a small template-method hierarchy:

- `AiHelper` — public interface, exposes only `getResponseFromPrompt(String)`.
- `AbstractAiHelper` — shared `HttpClient` + Jackson + `StopWatch` plumbing; declares `protected abstract getHttpRequest(...)` and `getBody(...)`.
- `impl/*HelperImpl` — one `final class` per provider, exposing `public static final AiHelper INSTANCE`.

Adding a new provider: extend `AbstractAiHelper`, override the two `protected` methods, expose an `AiHelper INSTANCE`.

## Tests

```sh
mvn test
```

(Currently just a placeholder.)
