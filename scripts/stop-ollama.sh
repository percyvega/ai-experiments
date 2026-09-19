#!/usr/bin/env bash
# Stops the local Ollama server started by start-ollama.sh.
set -euo pipefail

HOST="${OLLAMA_HOST:-http://localhost:11434}"

PIDS="$(pgrep -f 'ollama serve' || true)"
if [[ -z "$PIDS" ]]; then
    echo "No 'ollama serve' process is running"
    exit 0
fi

echo "Stopping ollama serve (pid: $(echo "$PIDS" | tr '\n' ' '))"
kill $PIDS 2>/dev/null || true

for _ in $(seq 1 10); do
    if ! pgrep -f 'ollama serve' >/dev/null; then
        break
    fi
    sleep 1
done

if pgrep -f 'ollama serve' >/dev/null; then
    echo "Still alive after 10s, sending SIGKILL" >&2
    pkill -9 -f 'ollama serve' || true
fi

if curl -fsS -m 2 "$HOST/api/tags" >/dev/null 2>&1; then
    echo "Something is still serving at $HOST (the Ollama desktop app restarts it)" >&2
    exit 1
fi
echo "Ollama stopped"
