#!/usr/bin/env bash
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:-}"

fail=0
err() { echo "FAIL: $1"; fail=1; }

if [ -z "$TASK" ]; then
    err "작업명을 인자로 전달해야 한다: agents/test/scripts/enforce-workflow.sh <작업명>"
else
    WORK="$ROOT/next-step/work/$TASK"
    [ -f "$WORK/00-task-card.md" ] || err "Test 시작 전 00-task-card.md가 필요하다: $WORK/00-task-card.md"
fi

for f in \
    "$ROOT/agents/test/AGENTS.md" \
    "$ROOT/agents/test/docs/workflow.md" \
    "$ROOT/agents/test/docs/testing-philosophy.md" \
    "$ROOT/agents/test/docs/tdd-workflow.md" \
    "$ROOT/agents/test/docs/test-double-policy.md" \
    "$ROOT/templates/01-red-test-report.md"
do
    [ -f "$f" ] || err "필수 하네스 파일 없음: $f"
done

# 작업 카드 컴파일 규칙 검증 (상위 docs 규칙이 카드에 컴파일됐는지)
if [ -n "$TASK" ] && [ -f "$ROOT/next-step/work/$TASK/00-task-card.md" ]; then
    "$ROOT/scripts/check-task-card.sh" "$TASK" || fail=1
fi

if [ "$fail" -eq 0 ]; then
    echo "OK: Test workflow gate passed"
fi
exit "$fail"
