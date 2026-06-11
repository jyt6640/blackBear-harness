#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(git rev-parse --show-toplevel)"
TASK=".local-agent-runner-test-$$"
WORK="$ROOT/next-step/work/$TASK"

cleanup() {
    rm -rf "$WORK"
}
trap cleanup EXIT

fail() {
    echo "FAIL: $1" >&2
    exit 1
}

mkdir -p "$WORK"
cat > "$WORK/00-task-card.md" <<EOF
# 00 Task Card

## 작업명

- $TASK

## 시작 기준 commit

- $(git -C "$ROOT" rev-parse HEAD)

## 필수 상위 문서

- \`docs/decisions/accepted/agent-handoff-by-artifact.md\`

## 역할별 추가 문서

### Test

- \`docs/workflow/tdd.md\`

### Feat

- \`docs/architecture/layered-architecture.md\`

### Refactor

- \`docs/workflow/refactoring.md\`

### Review

- \`docs/workflow/code-review.md\`
EOF

OUTPUT="$("$SCRIPT_DIR/run-stage.sh" test "$TASK" --profile unused --dry-run)"
printf '%s\n' "$OUTPUT" | grep -q 'agents/test/AGENTS.md' \
    || fail "Test 역할 하네스가 dry-run 입력에 없다"
printf '%s\n' "$OUTPUT" | grep -q 'docs/decisions/accepted/agent-handoff-by-artifact.md' \
    || fail "공통 상위 문서가 dry-run 입력에 없다"
printf '%s\n' "$OUTPUT" | grep -q 'docs/workflow/tdd.md' \
    || fail "Test 추가 문서가 dry-run 입력에 없다"
if printf '%s\n' "$OUTPUT" | grep -q 'docs/architecture/layered-architecture.md'; then
    fail "Feat 전용 문서가 Test 입력에 섞였다"
fi

if "$SCRIPT_DIR/run-stage.sh" unknown "$TASK" --profile unused --dry-run >/dev/null 2>&1; then
    fail "지원하지 않는 역할을 허용했다"
fi

if "$SCRIPT_DIR/run-stage.sh" test missing-task --profile unused --dry-run >/dev/null 2>&1; then
    fail "작업 카드 없이 dry-run을 허용했다"
fi

if "$SCRIPT_DIR/run-stage.sh" test ../outside --profile unused --dry-run >/dev/null 2>&1; then
    fail "경로 구분자가 있는 작업명을 허용했다"
fi

perl -0pi -e \
    's/## 역할별 추가 문서/- `docs\/does-not-exist.md`\n\n## 역할별 추가 문서/' \
    "$WORK/00-task-card.md"
if "$SCRIPT_DIR/run-stage.sh" test "$TASK" --profile unused --dry-run >/dev/null 2>&1; then
    fail "존재하지 않는 상위 문서를 허용했다"
fi

echo "OK: local agent runner tests passed"
