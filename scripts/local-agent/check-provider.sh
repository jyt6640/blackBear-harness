#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

usage() {
    echo "사용법: scripts/local-agent/check-provider.sh --profile <Codex profile>" >&2
}

PROFILE="${LOCAL_AGENT_PROFILE:-}"
while [ "$#" -gt 0 ]; do
    case "$1" in
        --profile)
            [ "$#" -ge 2 ] || { usage; exit 1; }
            PROFILE="$2"
            shift 2
            ;;
        *)
            usage
            exit 1
            ;;
    esac
done

[ -n "$PROFILE" ] || { usage; exit 1; }
local_agent_validate_name "profile" "$PROFILE"

PROFILE_PATH="$(local_agent_profile_path "$PROFILE")"
[ -f "$PROFILE_PATH" ] || {
    echo "FAIL: Codex profile이 없다: $PROFILE_PATH" >&2
    exit 1
}

TMP_ROOT="$(mktemp -d)"
trap 'rm -rf "$TMP_ROOT"' EXIT
mkdir -p "$TMP_ROOT/codex-home" "$TMP_ROOT/workspace"
cp "$PROFILE_PATH" "$TMP_ROOT/codex-home/config.toml"
cat > "$TMP_ROOT/workspace/AGENTS.md" <<'EOF'
# Provider Check Agent

요청된 명령 하나만 실행하고 짧게 답한다.
EOF
printf 'LOCAL_AGENT_READY\n' > "$TMP_ROOT/workspace/probe.txt"

CODEX_HOME="$TMP_ROOT/codex-home" codex -a never exec \
    --disable plugins \
    --disable apps \
    --disable tool_suggest \
    --disable multi_agent \
    --ephemeral \
    --skip-git-repo-check \
    -s read-only \
    -C "$TMP_ROOT/workspace" \
    -o "$TMP_ROOT/result.txt" \
    '셸 도구로 probe.txt를 읽고 그 내용을 정확히 출력해라. 다른 설명은 하지 마라.'

RESULT="$(tr -d '\r' < "$TMP_ROOT/result.txt" | tail -n 1)"
[ "$RESULT" = "LOCAL_AGENT_READY" ] || {
    echo "FAIL: provider 응답 또는 shell tool 호출이 예상과 다르다: $RESULT" >&2
    exit 1
}

echo "OK: local agent provider와 shell tool 호출 정상"
