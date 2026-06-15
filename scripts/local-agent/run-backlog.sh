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
[ -f "$BACKLOG" ] || { echo "FAIL: 백로그가 없다: $BACKLOG (카드 2장 이상이면 /orchestrate가 만든다)" >&2; exit 1; }
case "$PARALLEL" in ''|*[!0-9]*) echo "FAIL: --parallel은 양의 정수" >&2; exit 1 ;; esac
[ "$PARALLEL" -ge 1 ] || { echo "FAIL: --parallel은 1 이상" >&2; exit 1; }

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
    mkdir -p "$(dirname "$log")"

    git -C "$ROOT" worktree add --quiet "$wt" -b "loop/$task" 2>>"$log" || {
        echo "FAIL[$task]: worktree 생성 실패 (loop/$task 이미 존재?)"; return 1
    }

    local args=(--profile "$PROFILE" --max-retries "$MAX_RETRIES")
    [ "$HYBRID" = true ] && args+=(--hybrid)

    local rc=0
    ( cd "$wt" && scripts/local-agent/run-pipeline.sh "$task" "${args[@]}" ) >>"$log" 2>&1 || rc=$?

    git -C "$ROOT" worktree remove --force "$wt" 2>>"$log" || true

    case "$rc" in
        0) echo "OK[$task]: 완료 (로그: $log)" ;;
        2) echo "BLOCKED[$task]: 재시도 한도 초과 또는 BLOCKED — 사람 확인 필요 (로그: $log)" ;;
        *) echo "FAIL[$task]: rc=$rc (로그: $log)" ;;
    esac
    return 0
}

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

echo "백로그 루프 종료. BLOCKED/FAIL 카드는 사람이 검토하고, 완료 카드는 /orchestrate 마무리로 history 이동한다."
