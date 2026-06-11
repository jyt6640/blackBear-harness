#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

usage() {
    cat <<'EOF'
사용법:
  scripts/local-agent/run-pipeline.sh <작업명> --profile <Codex profile> [--max-retries 2]
EOF
}

[ "$#" -ge 1 ] || { usage >&2; exit 1; }

TASK="$1"
shift
PROFILE="${LOCAL_AGENT_PROFILE:-}"
MAX_RETRIES=2

while [ "$#" -gt 0 ]; do
    case "$1" in
        --profile)
            PROFILE="$2"
            shift 2
            ;;
        --max-retries)
            MAX_RETRIES="$2"
            shift 2
            ;;
        *)
            echo "FAIL: 알 수 없는 인자: $1" >&2
            usage >&2
            exit 1
            ;;
    esac
done

[ -n "$PROFILE" ] || {
    echo "FAIL: --profile 또는 LOCAL_AGENT_PROFILE이 필요하다" >&2
    exit 1
}
local_agent_validate_name "작업명" "$TASK"
local_agent_validate_name "profile" "$PROFILE"
case "$MAX_RETRIES" in
    ''|*[!0-9]*)
        echo "FAIL: --max-retries는 0 이상의 정수여야 한다" >&2
        exit 1
        ;;
esac

ROOT="$(local_agent_root)"
WORK="$(local_agent_work_dir "$ROOT" "$TASK")"
CARD="$WORK/00-task-card.md"
STATE_DIR="$WORK/.local-agent"
STATE="$STATE_DIR/state"
REVIEW_REPORT="$WORK/03-review-report.md"

[ -f "$CARD" ] || {
    echo "FAIL: 작업 카드가 없다: $CARD" >&2
    exit 1
}
mkdir -p "$STATE_DIR"
LOCK_DIR="$STATE_DIR/card.lock"
mkdir "$LOCK_DIR" 2>/dev/null || {
    echo "FAIL: 같은 카드의 로컬 에이전트가 이미 실행 중이다: $TASK" >&2
    exit 1
}
cleanup() {
    rmdir "$LOCK_DIR" 2>/dev/null || true
}
trap cleanup EXIT

run_stage() {
    LOCAL_AGENT_PIPELINE_LOCKED=1 \
        "$SCRIPT_DIR/run-stage.sh" "$1" "$TASK" --profile "$PROFILE"
}

run_from() {
    case "$1" in
        test)
            run_stage test
            run_stage feat
            run_stage refactor
            ;;
        feat)
            run_stage feat
            run_stage refactor
            ;;
        refactor)
            run_stage refactor
            ;;
        *)
            echo "FAIL: Review가 알 수 없는 재실행 단계를 반환했다: $1" >&2
            exit 1
            ;;
    esac
    run_stage review
}

INITIAL_BASE="$(local_agent_extract_first_value "$CARD" "## 시작 기준 commit")"
[ -n "$INITIAL_BASE" ] || {
    echo "FAIL: 작업 카드에 시작 기준 commit이 없다" >&2
    exit 1
}
printf 'review_base_ref=%s\nattempt=0\n' "$INITIAL_BASE" > "$STATE"

run_from test

ATTEMPT=0
while :; do
    VERDICT="$(local_agent_extract_first_value "$REVIEW_REPORT" "## 판정")"
    case "$VERDICT" in
        승인*)
            echo "OK: 자동 로컬 에이전트 파이프라인 승인"
            exit 0
            ;;
        반려*)
            ;;
        *)
            echo "FAIL: Review 판정을 해석할 수 없다: $VERDICT" >&2
            exit 1
            ;;
    esac

    if [ "$ATTEMPT" -ge "$MAX_RETRIES" ]; then
        echo "BLOCKED: Review 반려 재시도 한도($MAX_RETRIES)를 초과했다" >&2
        exit 2
    fi

    RESTART="$(local_agent_extract_first_value "$REVIEW_REPORT" "## 재실행 단계")"
    case "$RESTART" in
        test|feat|refactor) ;;
        *)
            echo "BLOCKED: Review가 유효한 재실행 단계를 지정하지 않았다: $RESTART" >&2
            exit 2
            ;;
    esac

    ATTEMPT=$((ATTEMPT + 1))
    RETRY_BASE="$(git -C "$ROOT" rev-parse HEAD)"
    printf 'review_base_ref=%s\nattempt=%s\nrestart=%s\n' \
        "$RETRY_BASE" "$ATTEMPT" "$RESTART" > "$STATE"
    echo "INFO: Review 반려로 $RESTART 단계부터 재실행한다 ($ATTEMPT/$MAX_RETRIES)"
    run_from "$RESTART"
done
