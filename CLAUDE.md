# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A personal sandbox for calling LLM providers (OpenAI, Anthropic, Google, Ollama) from Java. Two parallel approaches live
under `com.percyvega`:

- `plain` — direct HTTP via `java.net.http.HttpClient` + Jackson, no SDK.
- `langchain4j` — same providers via the [LangChain4j](https://docs.langchain4j.dev/) framework, extended into chat
  memory, AI Services, streaming, and embeddings.

There is no `Main`; experiments are driven from `src/test/java/...`. The `langchain4j` experiments are numbered `LC1`…
`LC12` and build on each other in order — when adding one, continue the numbering and keep the "one new idea per class"
shape.

## Experiments

Two kinds, and the distinction matters when running them:

**JUnit `@Test` classes** — `plain/P1GoogleTest`, `plain/P2AllModelsRefactoredTest`, `LC1GoogleUserTextTest`,
`LC2AllModelsUserTextTest`, `LC3UserMessageTest`, `LC4SystemAndUserMessagesTest`, `LC10EmbeddingTest`. All of them run
under a bare `mvn test`, since every name ends in `Test` (see Build & run).

**Interactive `main()` classes** — `LC5Chatting`, `LC6ChattingWithMemory`, `LC7Chatbot`, `LC8ChatbotWithAnnotations`,
`LC9ChatbotStreaming`, `LC11CompareEmbeddingAlgorithms`, `LC12EmbeddingStore`. They loop on `IO.readln(COMMAND_PROMPT)`
until an empty line. They are `main()` rather than `@Test` deliberately: a test runner gives no console to read from.
Don't convert them to tests.

## API keys

- `com.percyvega.utils.ApiKeys` reads keys from the **macOS keychain** by shelling out to
  `security find-generic-password`.
- Service names: `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`, `GOOGLE_API_KEY`. This is macOS-only — there is no env-var or
  `.env` fallback.
- Ollama is local (`http://localhost:11434`) and needs no key.
- To add a new provider key, add a method to `ApiKeys` and store it in keychain via
  `security add-generic-password -a "$USER" -s NEW_KEY_NAME -w <key>`.
