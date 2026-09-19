#!/usr/bin/env bash
# Starts a local Ollama server in the background, waits until it answers, and
# checks that the model the experiments use is pulled.
set -euo pipefail

HOST="${OLLAMA_HOST:-http://localhost:11434}"
MODEL="${OLLAMA_MODEL:-mistral-small3.2}" # keep in sync with Constants.MISTRAL_AI_MODEL_NAME
TMP_DIR="${TMPDIR:-/tmp}"
LOG="${TMP_DIR%/}/ollama-serve.log" # TMPDIR carries a trailing slash on macOS

if curl -fsS -m 2 "$HOST/api/tags" >/dev/null 2>&1; then
    echo "Ollama is already running at $HOST"
else
    if ! command -v ollama >/dev/null 2>&1; then
        echo "ollama is not installed: brew install ollama" >&2
        exit 1
    fi

    echo "Starting ollama serve (log: $LOG)"
    nohup ollama serve >>"$LOG" 2>&1 &

    for _ in $(seq 1 30); do
        if curl -fsS -m 2 "$HOST/api/tags" >/dev/null 2>&1; then
            break
        fi
        sleep 1
    done

    if ! curl -fsS -m 2 "$HOST/api/tags" >/dev/null 2>&1; then
        echo "Ollama did not come up within 30s; see $LOG" >&2
        exit 1
    fi
    echo "Ollama is up at $HOST"
fi

if ollama list | awk 'NR > 1 {print $1}' | grep -q "^${MODEL}\(:latest\)\?$"; then
    echo "Model $MODEL is available"
else
    echo "Model $MODEL is not pulled yet: ollama pull $MODEL" >&2
fi
