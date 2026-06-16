#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"

usage() {
    cat <<'EOF'
사용법:
  scripts/local-agent/run-stage.sh <test|feat|refactor|review> <작업명> --profile <Codex profile> [--dry-run]
EOF
}

[ "$#" -ge 2 ] || { usage >&2; exit 1; }

ROLE="$1"
TASK="$2"
shift 2

PROFILE="${LOCAL_AGENT_PROFILE:-}"
DRY_RUN=false

while [ "$#" -gt 0 ]; do
    case "$1" in
        --profile)
            [ "$#" -ge 2 ] || { usage >&2; exit 1; }
            PROFILE="$2"
            shift 2
            ;;
        --dry-run)
            DRY_RUN=true
            shift
            ;;
        *)
            echo "FAIL: 알 수 없는 인자: $1" >&2
            usage >&2
            exit 1
            ;;
    esac
done

case "$ROLE" in
    test) ROLE_HEADING="Test"; REPORT="01-red-test-report.md"; SANDBOX="workspace-write" ;;
    feat) ROLE_HEADING="Feat"; REPORT="02-green-implementation-report.md"; SANDBOX="workspace-write" ;;
    refactor) ROLE_HEADING="Refactor"; REPORT="03-refactor-report.md"; SANDBOX="workspace-write" ;;
    review) ROLE_HEADING="Review"; REPORT="04-review-report.md"; SANDBOX="workspace-write" ;;
    *)
        echo "FAIL: 지원하지 않는 역할: $ROLE" >&2
        usage >&2
        exit 1
        ;;
esac

[ -n "$PROFILE" ] || {
    echo "FAIL: --profile 또는 LOCAL_AGENT_PROFILE이 필요하다" >&2
    exit 1
}
ROOT="$(local_agent_root)"
local_agent_validate_task_name "$ROOT" "$TASK"
local_agent_validate_name "profile" "$PROFILE"

WORK="$(local_agent_work_dir "$ROOT" "$TASK")"
CARD="$WORK/00-task-card.md"
PROFILE_PATH="$(local_agent_profile_path "$PROFILE")"

[ -f "$CARD" ] || {
    echo "FAIL: 작업 카드가 없다: $CARD" >&2
    exit 1
}

BASE_REF=""
if [ "$ROLE" = "review" ]; then
    BASE_REF="$(local_agent_review_base_ref "$ROOT" "$TASK" "$CARD")"
    [ -n "$BASE_REF" ] || {
        echo "FAIL: Review 기준 commit을 찾을 수 없다" >&2
        exit 1
    }
    "$ROOT/agents/$ROLE/scripts/enforce-workflow.sh" "$TASK" "$BASE_REF"
else
    "$ROOT/agents/$ROLE/scripts/enforce-workflow.sh" "$TASK"
fi

INPUTS="$(mktemp)"
COMMON_INPUTS="$(mktemp)"
ROLE_INPUTS="$(mktemp)"
TMP_ROOT=""
LOCK_DIR=""
cleanup() {
    rm -f "$INPUTS" "$COMMON_INPUTS" "$ROLE_INPUTS"
    [ -z "$TMP_ROOT" ] || rm -rf "$TMP_ROOT"
    [ -z "$LOCK_DIR" ] || rmdir "$LOCK_DIR" 2>/dev/null || true
}
trap cleanup EXIT

printf '%s\n' \
    "agents/$ROLE/AGENTS.md" \
    "next-step/work/$TASK/00-task-card.md" > "$INPUTS"
find "$ROOT/agents/$ROLE/docs" -maxdepth 1 -type f -name '*.md' \
    | sed "s|^$ROOT/||" | sort >> "$INPUTS"

local_agent_extract_section_paths "$CARD" "## 필수 상위 문서" > "$COMMON_INPUTS"
local_agent_extract_role_paths "$CARD" "$ROLE_HEADING" > "$ROLE_INPUTS"
[ -s "$COMMON_INPUTS" ] || {
    echo "FAIL: 작업 카드의 필수 상위 문서 목록이 비어 있다" >&2
    exit 1
}
[ -s "$ROLE_INPUTS" ] || {
    echo "FAIL: 작업 카드의 $ROLE_HEADING 추가 문서 목록이 비어 있다" >&2
    exit 1
}
cat "$COMMON_INPUTS" "$ROLE_INPUTS" >> "$INPUTS"

case "$ROLE" in
    feat)
        printf '%s\n' "next-step/work/$TASK/01-red-test-report.md" >> "$INPUTS"
        ;;
    refactor)
        [ ! -f "$WORK/01-red-test-report.md" ] || printf '%s\n' "next-step/work/$TASK/01-red-test-report.md" >> "$INPUTS"
        [ ! -f "$WORK/02-green-implementation-report.md" ] || printf '%s\n' "next-step/work/$TASK/02-green-implementation-report.md" >> "$INPUTS"
        ;;
    review)
        for artifact in 01-red-test-report.md 02-green-implementation-report.md 03-refactor-report.md; do
            [ ! -f "$WORK/$artifact" ] || printf '%s\n' "next-step/work/$TASK/$artifact" >> "$INPUTS"
        done
        printf '%s\n' "templates/06-scorecard.md" >> "$INPUTS"
        printf '%s\n' "docs/workflow/loop-scoring-criteria.md" >> "$INPUTS"
        ;;
esac

sort -u "$INPUTS" -o "$INPUTS"
while IFS= read -r path; do
    local_agent_validate_relative_file "$ROOT" "$path"
done < "$INPUTS"

if [ "$DRY_RUN" = true ]; then
    echo "ROLE=$ROLE"
    echo "TASK=$TASK"
    echo "PROFILE=$PROFILE"
    echo "SANDBOX=$SANDBOX"
    [ -z "$BASE_REF" ] || echo "REVIEW_BASE_REF=$BASE_REF"
    echo "INPUTS:"
    sed 's/^/- /' "$INPUTS"
    exit 0
fi

[ -f "$PROFILE_PATH" ] || {
    echo "FAIL: Codex profile이 없다: $PROFILE_PATH" >&2
    exit 1
}

if [ "${LOCAL_AGENT_PIPELINE_LOCKED:-0}" != "1" ]; then
    mkdir -p "$WORK/.local-agent"
    LOCK_DIR="$WORK/.local-agent/card.lock"
    mkdir "$LOCK_DIR" 2>/dev/null || {
        echo "FAIL: 같은 카드의 로컬 에이전트가 이미 실행 중이다: $TASK" >&2
        exit 1
    }
fi

local_agent_assert_clean_worktree "$ROOT"
INPUT_HEAD="$(git -C "$ROOT" rev-parse HEAD)"
rm -f "$WORK/$REPORT"
[ "$ROLE" != "review" ] || rm -f "$WORK/06-scorecard.md"

TMP_ROOT="$(mktemp -d)"
mkdir -p "$TMP_ROOT/codex-home" "$TMP_ROOT/workspace"
cp "$PROFILE_PATH" "$TMP_ROOT/codex-home/config.toml"
cp "$ROOT/agents/$ROLE/AGENTS.md" "$TMP_ROOT/workspace/AGENTS.md"

mkdir -p "$WORK/.local-agent"
OUTPUT="$WORK/.local-agent/$ROLE-last-message.txt"
PROMPT="$TMP_ROOT/prompt.md"

{
    echo "# Local $ROLE_HEADING Agent Run"
    echo
    echo "저장소 루트: \`$ROOT\`"
    echo "작업명: \`$TASK\`"
    echo "입력 HEAD: \`$INPUT_HEAD\`"
    [ -z "$BASE_REF" ] || echo "Review 기준 commit: \`$BASE_REF\`"
    echo
    echo "## 이번 작업 카드 (1차 구속 계약)"
    echo
    echo "아래 작업 카드의 '이번 작업 컴파일 규칙'을 1차 구속 계약으로 따른다."
    echo "오케스트레이터가 상위 docs에서 이번 작업 규칙을 이미 컴파일했다. 이 규칙을 우선 적용하고, 모호할 때만 아래 참고 문서의 출처를 확인한다."
    echo
    echo '```markdown'
    cat "$ROOT/next-step/work/$TASK/00-task-card.md"
    echo '```'
    echo
    echo "참고 문서 (카드가 모호할 때 출처 확인용):"
    while IFS= read -r path; do
        printf -- '- `%s/%s`\n' "$ROOT" "$path"
    done < "$INPUTS"
    echo
    if [ "$ROLE" = "review" ]; then
        echo '판정과 함께 `next-step/work/'"$TASK"'/06-scorecard.md`를 templates/06-scorecard.md 형식으로 작성해 전 항목을 채점한다. 감점에는 코드 위치 근거를 인용한다.'
        echo
    fi
    cat <<EOF
모든 shell 명령은 먼저 \`cd "$ROOT"\`한 뒤 실행한다.
저장소 루트에서 작업하고 역할 AGENTS.md의 책임, 금지, 완료 기준을 따른다.
전체 docs를 탐색해 새 규칙을 추가하지 않는다. 입력 문서가 모호하거나 decision이
필요하면 추측하지 말고 보고서에 BLOCKED로 기록하고 종료한다.

역할 workflow gate는 실행기가 통과시켰다. 필요한 테스트와 검증 명령은 직접 실행한다.
역할이 허용하는 코드와 \`next-step/work/$TASK/$REPORT\`만 변경한다.
Test, Feat, Refactor는 역할 규칙에 맞는 커밋을 만들고 worktree를 깨끗하게
남긴다. Review는 코드와 커밋을 변경하지 않고 보고서만 작성한다.
보고서의 \`## 실제 참조 문서\`에는 실제로 확인한 문서만 위 참고 문서 경로 형식으로 기록한다.

자동 모드이므로 다음 역할을 호출하거나 사용자에게 다음 스킬을 요청하지 않는다.
완료 후 수행 결과만 짧게 반환하고 종료한다.
EOF
} > "$PROMPT"

CODEX_HOME="$TMP_ROOT/codex-home" codex -a never exec \
    --disable plugins \
    --disable apps \
    --disable tool_suggest \
    --disable multi_agent \
    --ephemeral \
    --skip-git-repo-check \
    -s "$SANDBOX" \
    -C "$TMP_ROOT/workspace" \
    --add-dir "$ROOT" \
    -o "$OUTPUT" \
    - < "$PROMPT"

[ -f "$WORK/$REPORT" ] || {
    echo "FAIL: $ROLE 단계 보고서가 생성되지 않았다: $WORK/$REPORT" >&2
    exit 1
}
if [ "$ROLE" = "review" ] && [ ! -f "$WORK/06-scorecard.md" ]; then
    echo "FAIL: Review 단계가 06-scorecard.md(철학 점수표)를 생성하지 않았다" >&2
    exit 1
fi

case "$ROLE" in
    test)
        local_agent_assert_commit_type "$ROOT" "$INPUT_HEAD" test no
        local_agent_assert_stage_paths "$ROOT" "$INPUT_HEAD" test
        ;;
    feat)
        local_agent_assert_commit_type "$ROOT" "$INPUT_HEAD" feat no
        local_agent_assert_stage_paths "$ROOT" "$INPUT_HEAD" feat
        ;;
    refactor)
        local_agent_assert_commit_type "$ROOT" "$INPUT_HEAD" refactor yes
        ;;
    review)
        [ "$(git -C "$ROOT" rev-parse HEAD)" = "$INPUT_HEAD" ] || {
            echo "FAIL: Review Agent가 커밋을 생성했다" >&2
            exit 1
        }
        local_agent_assert_review_report_schema "$WORK/$REPORT"
        ;;
esac

local_agent_assert_referenced_docs "$WORK/$REPORT" "$INPUTS"
local_agent_assert_clean_worktree "$ROOT"
case "$ROLE" in
    feat|refactor)
        "$ROOT/scripts/check-philosophy.sh"
        local_agent_verify_project "$ROOT"
        local_agent_assert_clean_worktree "$ROOT"
        ;;
esac
echo "OK: $ROLE 단계 완료"
