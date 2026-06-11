#!/usr/bin/env bash
set -u

local_agent_root() {
    git rev-parse --show-toplevel
}

local_agent_work_dir() {
    printf '%s/next-step/work/%s\n' "$1" "$2"
}

local_agent_profile_path() {
    local codex_home="${CODEX_HOME:-$HOME/.codex}"
    printf '%s/%s.config.toml\n' "$codex_home" "$1"
}

local_agent_validate_name() {
    local label="$1"
    local value="$2"

    case "$value" in
        ""|.|..|*/*|*\\*)
            echo "FAIL: $label 값에 디렉터리 구분자를 사용할 수 없다: $value" >&2
            return 1
            ;;
    esac
}

local_agent_extract_section_paths() {
    local card="$1"
    local heading="$2"

    awk -v heading="$heading" '
        $0 == heading { found=1; next }
        found && /^## / { exit }
        found { print }
    ' "$card" | sed -n 's/^- `\([^`]*\)`.*/\1/p'
}

local_agent_extract_role_paths() {
    local card="$1"
    local role_heading="$2"

    awk -v heading="### $role_heading" '
        $0 == heading { found=1; next }
        found && /^### / { exit }
        found && /^## / { exit }
        found { print }
    ' "$card" | sed -n 's/^- `\([^`]*\)`.*/\1/p'
}

local_agent_extract_first_value() {
    local file="$1"
    local heading="$2"

    awk -v heading="$heading" '
        $0 == heading { found=1; next }
        found && /^## / { exit }
        found && NF {
            sub(/^- /, "")
            print
            exit
        }
    ' "$file"
}

local_agent_validate_relative_file() {
    local root="$1"
    local path="$2"

    case "$path" in
        /*|*..*)
            echo "FAIL: 작업 카드 문서 경로는 저장소 내부 상대 경로여야 한다: $path" >&2
            return 1
            ;;
    esac
    [ -f "$root/$path" ] || {
        echo "FAIL: 작업 카드에 지정된 문서가 없다: $path" >&2
        return 1
    }
}

local_agent_review_base_ref() {
    local root="$1"
    local task="$2"
    local card="$3"
    local state="$root/next-step/work/$task/.local-agent/state"
    local base=""

    if [ -f "$state" ]; then
        base=$(sed -n 's/^review_base_ref=//p' "$state" | tail -n 1)
    fi
    if [ -z "$base" ]; then
        base=$(local_agent_extract_first_value "$card" "## 시작 기준 commit")
    fi
    printf '%s\n' "$base"
}

local_agent_assert_clean_tracked_tree() {
    local root="$1"
    local dirty
    dirty=$(git -C "$root" status --porcelain --untracked-files=no)
    [ -z "$dirty" ] || {
        echo "FAIL: 로컬 에이전트 실행 전 tracked worktree가 깨끗해야 한다" >&2
        printf '%s\n' "$dirty" >&2
        return 1
    }
}

local_agent_assert_commit_type() {
    local root="$1"
    local base="$2"
    local expected="$3"
    local allow_empty="$4"
    local count=0
    local subject type

    while IFS= read -r subject; do
        [ -n "$subject" ] || continue
        count=$((count + 1))
        type=$(printf '%s' "$subject" | sed -E 's/^([a-z]+).*/\1/')
        if [ "$type" != "$expected" ]; then
            echo "FAIL: $expected 단계에서 다른 type 커밋이 생성됐다: $subject" >&2
            return 1
        fi
    done < <(git -C "$root" log --reverse --no-merges --format='%s' "$base..HEAD")

    if [ "$allow_empty" != "yes" ] && [ "$count" -eq 0 ]; then
        echo "FAIL: $expected 단계가 커밋을 만들지 않았다" >&2
        return 1
    fi
}
