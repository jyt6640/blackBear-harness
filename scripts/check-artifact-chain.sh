#!/usr/bin/env bash
# 산출물 체인 검사: 이전 단계 산출물 없이 다음 단계 산출물이 존재하면 위반이다.
# 역할별 enforce-workflow.sh가 시작 게이트라면, 이 검사는 체인 전체의 사후 정합성 판정이다.
# 사용: check-artifact-chain.sh <작업명> [--work-root <경로>]
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:?사용법: check-artifact-chain.sh <작업명> [--work-root <경로>]}"
shift
WORK_ROOT="$ROOT/next-step/work"
while [ "$#" -gt 0 ]; do
    case "$1" in
        --work-root) WORK_ROOT="$2"; shift 2 ;;
        *) echo "FAIL: 알 수 없는 인자: $1" >&2; exit 1 ;;
    esac
done

work="$WORK_ROOT/$TASK"
[ -d "$work" ] || { echo "FAIL: 작업 디렉토리가 없다: $work"; exit 1; }

fail=0
err() { echo "FAIL: $1"; fail=1; }

[ -f "$work/00-task-card.md" ] || err "00-task-card.md 없음 - 작업 정의가 먼저다"

if [ -f "$work/02-implementation-report.md" ] && [ ! -f "$work/01-test-report.md" ]; then
    err "01-test-report.md 없이 02-implementation-report.md 존재 (Test 단계 생략 금지)"
fi
if [ -f "$work/03-review-report.md" ] && [ ! -f "$work/02-implementation-report.md" ] && [ ! -f "$work/02-refactor-report.md" ]; then
    err "02 보고서 없이 03-review-report.md 존재 (Feat / Refactor 단계 생략 금지)"
fi
if [ -f "$work/03-review-report.md" ] && [ ! -f "$work/05-scorecard.md" ]; then
    err "05-scorecard.md 없이 03-review-report.md 존재 (Review는 판정과 함께 철학 점수표를 작성한다)"
fi
if [ -f "$work/04-summary.md" ] && [ ! -f "$work/03-review-report.md" ]; then
    err "03-review-report.md 없이 04-summary.md 존재 (승인 없이 마무리 금지)"
fi

if [ "$fail" -eq 0 ]; then
    echo "OK: 산출물 체인 정상 ($TASK)"
fi
exit "$fail"
