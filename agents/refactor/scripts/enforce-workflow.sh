#!/usr/bin/env bash
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:-}"

fail=0
err() { echo "FAIL: $1"; fail=1; }

if [ -z "$TASK" ]; then
    err "작업명을 인자로 전달해야 한다: agents/refactor/scripts/enforce-workflow.sh <작업명>"
else
    WORK="$ROOT/next-step/work/$TASK"
    [ -f "$WORK/00-task-card.md" ] || err "Refactor 시작 전 00-task-card.md 또는 리팩터링 작업 카드가 필요하다: $WORK/00-task-card.md"
    if [ -f "$WORK/01-test-report.md" ] && [ ! -f "$WORK/02-implementation-report.md" ]; then
        err "feature 카드에서는 Feat 단계(02-implementation-report.md) 이후에 Refactor를 시작한다"
    fi
fi

for f in \
    "$ROOT/agents/refactor/AGENTS.md" \
    "$ROOT/agents/refactor/docs/workflow.md" \
    "$ROOT/agents/refactor/docs/refactoring-philosophy.md" \
    "$ROOT/agents/refactor/docs/behavior-preservation.md" \
    "$ROOT/next-step/templates/02-refactor-report.md"
do
    [ -f "$f" ] || err "필수 하네스 파일 없음: $f"
done

# 작업 카드 컴파일 규칙 검증 (상위 docs 규칙이 카드에 컴파일됐는지)
if [ -n "$TASK" ] && [ -f "$ROOT/next-step/work/$TASK/00-task-card.md" ]; then
    "$ROOT/scripts/check-task-card.sh" "$TASK" || fail=1
fi

if [ "$fail" -eq 0 ]; then
    echo "OK: Refactor workflow gate passed"
fi
exit "$fail"
