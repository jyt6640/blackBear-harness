#!/usr/bin/env bash
# 백로그 무인 순회 루프. 백로그의 '대기' 카드를 worktree로 격리해 병렬/직렬 실행한다.
# 느린 로컬 LLM을 사람이 기다리지 않고, 던져놓고 떠나는 구조다.
# 각 티켓은 run-pipeline(기본 --hybrid)로 돌고, 게이트와 재시도 한도를 그대로 따른다.
#
# 사용:
#   scripts/local-agent/run-backlog.sh --profile <p> [--parallel N] [--max-retries 2] [--full-local] [--dry-run]
#   --parallel N : 동시에 N개 worktree (기본 1=직렬)
#   --full-local : Review까지 로컬 (기본은 hybrid: 구현만 로컬)
#   --dry-run    : 실행 계획만 출력 (LLM 호출 없음)
# → docs/decisions/accepted/backlog-loop-worktree.md
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

ROOT="$(git rev-parse --show-toplevel)"
BACKLOG="$ROOT/next-step/work/backlog.md"
PROFILE="${LOCAL_AGENT_PROFILE:-}"
PARALLEL=1
MAX_RETRIES=2
HYBRID=true
DRY_RUN=false

while [ "$#" -gt 0 ]; do
    case "$1" in
        --profile) PROFILE="$2"; shift 2 ;;
        --parallel) PARALLEL="$2"; shift 2 ;;
        --max-retries) MAX_RETRIES="$2"; shift 2 ;;
        --full-local) HYBRID=false; shift ;;
        --dry-run) DRY_RUN=true; shift ;;
        *) echo "FAIL: 알 수 없는 인자: $1" >&2; exit 1 ;;
    esac
done

[ -n "$PROFILE" ] || { echo "FAIL: --profile이 필요하다" >&2; exit 1; }
local_agent_validate_name "profile" "$PROFILE" || exit 1
[ -f "$BACKLOG" ] || { echo "FAIL: 백로그가 없다: $BACKLOG (카드 2장 이상이면 /orchestrate가 만든다)" >&2; exit 1; }
case "$PARALLEL" in ''|*[!0-9]*) echo "FAIL: --parallel은 양의 정수" >&2; exit 1 ;; esac
[ "$PARALLEL" -ge 1 ] || { echo "FAIL: --parallel은 1 이상" >&2; exit 1; }
case "$MAX_RETRIES" in ''|*[!0-9]*) echo "FAIL: --max-retries는 0 이상의 정수" >&2; exit 1 ;; esac

# 백로그 카드 목록에서 '대기' 상태 작업명 추출 (| 순번 | 작업명 | 행위 | 상태 | 비고 |)
PENDING=()
while IFS= read -r name; do
    [ -n "$name" ] && PENDING+=("$name")
done < <(
    awk -F'|' '/^\| *[0-9]+ *\|/ {
        name=$3; status=$5;
        gsub(/^[ \t]+|[ \t]+$/, "", name);
        gsub(/^[ \t]+|[ \t]+$/, "", status);
        if (name != "" && status == "대기") print name;
    }' "$BACKLOG"
)

[ "${#PENDING[@]:-0}" -gt 0 ] || { echo "INFO: 백로그에 '대기' 카드가 없다. 끝났거나 아직 컴파일되지 않았다."; exit 0; }

MODE=$([ "$HYBRID" = true ] && echo "hybrid(구현=로컬, Review=강모델)" || echo "full-local")
echo "백로그 루프: 대기 ${#PENDING[@]}장, 병렬 $PARALLEL, 모드 $MODE, 재시도 $MAX_RETRIES"
printf '  - %s\n' "${PENDING[@]}"

if [ "$DRY_RUN" = true ]; then
    echo
    echo "[dry-run] 각 카드 실행 계획:"
    for t in "${PENDING[@]}"; do
        wt="$ROOT/../blackBear-wt-$t"
        echo "  $t: worktree=$wt"
        echo "      git worktree add \"$wt\" -b loop/$t"
        cmd="run-pipeline.sh $t --profile $PROFILE --max-retries $MAX_RETRIES"
        [ "$HYBRID" = true ] && cmd="$cmd --hybrid"
        echo "      (worktree에서) scripts/local-agent/$cmd"
        echo "      git worktree remove \"$wt\""
    done
    exit 0
fi

run_one() {
    local task="$1"
    local wt="$ROOT/../blackBear-wt-$task"
    local log="$ROOT/next-step/work/$task/.local-agent/backlog-run.log"
    local source_work="$ROOT/next-step/work/$task"
    local target_work="$wt/next-step/work/$task"
    local result="$RESULT_DIR/$task"
    local input_archive="$RESULT_DIR/$task-input.tar"
    local output_archive="$RESULT_DIR/$task-output.tar"
    local rc=0

    local_agent_validate_task_name "$ROOT" "$task" || {
        printf 'FAIL\n' > "$result"
        return 0
    }
    [ -d "$source_work" ] || {
        echo "FAIL[$task]: 작업 카드 디렉터리가 없다: $source_work"
        printf 'FAIL\n' > "$result"
        return 0
    }
    mkdir -p "$(dirname "$log")"

    git -C "$ROOT" worktree add --quiet "$wt" -b "loop/$task" 2>>"$log" || {
        echo "FAIL[$task]: worktree 생성 실패 (loop/$task 이미 존재?)"
        printf 'FAIL\n' > "$result"
        return 0
    }

    mkdir -p "$target_work"
    tar -C "$source_work" --exclude='./.local-agent' -cf "$input_archive" . || {
        echo "FAIL[$task]: 작업 메모리 묶기 실패 (worktree 보존: $wt)"
        printf 'FAIL\n' > "$result"
        return 0
    }
    tar -C "$target_work" -xf "$input_archive" || {
        echo "FAIL[$task]: 작업 메모리 전달 실패 (worktree 보존: $wt)"
        printf 'FAIL\n' > "$result"
        return 0
    }

    local args=(--profile "$PROFILE" --max-retries "$MAX_RETRIES")
    [ "$HYBRID" = true ] && args+=(--hybrid)

    ( cd "$wt" && scripts/local-agent/run-pipeline.sh "$task" "${args[@]}" ) >>"$log" 2>&1 || rc=$?

    if [ -d "$target_work" ]; then
        tar -C "$target_work" --exclude='./.local-agent/card.lock' -cf "$output_archive" . \
            && tar -C "$source_work" -xf "$output_archive" \
            || rc=1
    fi

    case "$rc" in
        0)
            git -C "$ROOT" worktree remove --force "$wt" 2>>"$log" || rc=1
            if [ "$rc" -eq 0 ]; then
                echo "OK[$task]: 완료 (로그: $log)"
                printf 'OK\n' > "$result"
            else
                echo "FAIL[$task]: worktree 정리 실패 (로그: $log)"
                printf 'FAIL\n' > "$result"
            fi
            ;;
        2)
            echo "BLOCKED[$task]: 사람 확인 필요 (worktree 보존: $wt, 로그: $log)"
            printf 'BLOCKED\n' > "$result"
            ;;
        *)
            echo "FAIL[$task]: rc=$rc (worktree 보존: $wt, 로그: $log)"
            printf 'FAIL\n' > "$result"
            ;;
    esac
    return 0
}

RESULT_DIR="$(mktemp -d)"
trap 'rm -rf "$RESULT_DIR"' EXIT
active=0
for task in "${PENDING[@]}"; do
    run_one "$task" &
    active=$((active + 1))
    if [ "$active" -ge "$PARALLEL" ]; then
        wait          # 묶음이 다 끝나면 다음 묶음 시작 (bash 3.2 호환)
        active=0
    fi
done
wait

fail_count=$(grep -l '^FAIL$' "$RESULT_DIR"/* 2>/dev/null | wc -l | tr -d ' ')
blocked_count=$(grep -l '^BLOCKED$' "$RESULT_DIR"/* 2>/dev/null | wc -l | tr -d ' ')
echo "백로그 루프 종료: FAIL=$fail_count BLOCKED=$blocked_count"
[ "$fail_count" -eq 0 ] || exit 1
[ "$blocked_count" -eq 0 ] || exit 2
exit 0
