#!/usr/bin/env bash
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:-}"

fail=0
err() { echo "FAIL: $1"; fail=1; }

if [ -z "$TASK" ]; then
    err "작업명을 인자로 전달해야 한다: agents/feat/scripts/enforce-workflow.sh <작업명>"
else
    WORK="$ROOT/next-step/work/$TASK"
    [ -f "$WORK/00-task-card.md" ] || err "Feat 시작 전 00-task-card.md가 필요하다: $WORK/00-task-card.md"
    [ -f "$WORK/01-test-report.md" ] || err "Feat 시작 전 01-test-report.md가 필요하다: $WORK/01-test-report.md"
fi

for f in \
    "$ROOT/agents/feat/AGENTS.md" \
    "$ROOT/agents/feat/docs/workflow.md" \
    "$ROOT/agents/feat/docs/implementation-philosophy.md" \
    "$ROOT/agents/feat/docs/layer-responsibility.md" \
    "$ROOT/agents/feat/docs/minimal-implementation.md" \
    "$ROOT/next-step/templates/02-implementation-report.md"
do
    [ -f "$f" ] || err "필수 하네스 파일 없음: $f"
done

# 작업 카드 컴파일 규칙 검증 (상위 docs 규칙이 카드에 컴파일됐는지)
if [ -n "$TASK" ] && [ -f "$ROOT/next-step/work/$TASK/00-task-card.md" ]; then
    "$ROOT/scripts/check-task-card.sh" "$TASK" || fail=1
fi

# 기계 판정 철학 위반 사전 차단 (약한 모델 위반을 리뷰 전에 막는다)
"$ROOT/scripts/check-philosophy.sh" || fail=1

if [ "$fail" -eq 0 ]; then
    echo "OK: Feat workflow gate passed"
fi
exit "$fail"
