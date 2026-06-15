#!/usr/bin/env bash
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:-}"
BASE_REF="${2:-}"

fail=0
err() { echo "FAIL: $1"; fail=1; }

if [ -z "$TASK" ]; then
    err "작업명을 인자로 전달해야 한다: agents/review/scripts/enforce-workflow.sh <작업명>"
else
    WORK="$ROOT/next-step/work/$TASK"
    [ -f "$WORK/00-task-card.md" ] || err "Review 시작 전 00-task-card.md가 필요하다: $WORK/00-task-card.md"
    if [ -f "$WORK/02-implementation-report.md" ]; then
        # feature 카드: Test → Feat → Refactor 산출물이 모두 필요하다
        [ -f "$WORK/01-test-report.md" ] || err "Review 시작 전 01-test-report.md가 필요하다: $WORK/01-test-report.md"
        [ -f "$WORK/02-refactor-report.md" ] || err "Review 시작 전 02-refactor-report.md가 필요하다 (개선할 것이 없어도 행위 보존 확인을 기록한다): $WORK/02-refactor-report.md"
    elif [ -f "$WORK/02-refactor-report.md" ]; then
        : # refactor 전용 카드: 행위 변경이 없으므로 01 / 02-implementation 없이 진행할 수 있다
    else
        err "Review 시작 전 02-implementation-report.md(feature) 또는 02-refactor-report.md(refactor 전용)가 필요하다"
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

if [ -n "$BASE_REF" ]; then
    "$ROOT/scripts/check-commit-chain.sh" "$BASE_REF" || fail=1
else
    echo "INFO: 기준 ref가 없어 커밋 체인 검사를 건너뛴다. 00-task-card의 시작 기준 commit으로 실행한다: agents/review/scripts/enforce-workflow.sh <작업명> <시작ref>"
fi

# 작업 카드 컴파일 규칙 검증 (상위 docs 규칙이 카드에 컴파일됐는지)
if [ -n "$TASK" ] && [ -f "$ROOT/next-step/work/$TASK/00-task-card.md" ]; then
    "$ROOT/scripts/check-task-card.sh" "$TASK" || fail=1
fi

if [ "$fail" -eq 0 ]; then
    echo "OK: Review workflow gate passed"
fi
exit "$fail"
