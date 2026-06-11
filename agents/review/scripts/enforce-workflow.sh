#!/usr/bin/env bash
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:-}"

fail=0
err() { echo "FAIL: $1"; fail=1; }

if [ -z "$TASK" ]; then
    err "작업명을 인자로 전달해야 한다: agents/review/scripts/enforce-workflow.sh <작업명>"
else
    WORK="$ROOT/next-step/work/$TASK"
    [ -f "$WORK/00-task-card.md" ] || err "Review 시작 전 00-task-card.md가 필요하다: $WORK/00-task-card.md"
    if [ -f "$WORK/02-refactor-report.md" ]; then
        : # refactor 카드: 행위 변경이 없으므로 01-test-report 없이 진행할 수 있다
    else
        [ -f "$WORK/01-test-report.md" ] || err "Review 시작 전 01-test-report.md가 필요하다: $WORK/01-test-report.md"
        [ -f "$WORK/02-implementation-report.md" ] || err "Review 시작 전 02-implementation-report.md가 필요하다 (refactor 카드라면 02-refactor-report.md): $WORK/02-implementation-report.md"
    fi
fi

for f in \
    "$ROOT/agents/review/AGENTS.md" \
    "$ROOT/agents/review/docs/workflow.md" \
    "$ROOT/agents/review/docs/review-philosophy.md" \
    "$ROOT/agents/review/docs/rejection-criteria.md" \
    "$ROOT/agents/review/docs/final-checklist.md" \
    "$ROOT/next-step/templates/03-review-report.md"
do
    [ -f "$f" ] || err "필수 하네스 파일 없음: $f"
done

if [ "$fail" -eq 0 ]; then
    echo "OK: Review workflow gate passed"
fi
exit "$fail"
