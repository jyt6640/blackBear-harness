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
        ""|[!A-Za-z0-9]*|*[!A-Za-z0-9._-]*|*..*|*.)
            echo "FAIL: $label은 영문/숫자로 시작하고 영문/숫자/._-만 사용할 수 있다: $value" >&2
            return 1
            ;;
    esac
}

local_agent_validate_task_name() {
    local root="$1"
    local task="$2"

    local_agent_validate_name "작업명" "$task" || return 1
    git -C "$root" check-ref-format --branch "loop/$task" >/dev/null 2>&1 || {
        echo "FAIL: 작업명을 안전한 branch 이름으로 사용할 수 없다: $task" >&2
        return 1
    }
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

local_agent_extract_frontmatter_value() {
    local file="$1"
    local key="$2"

    awk -v key="$key" '
        NR == 1 && $0 == "---" { frontmatter=1; next }
        frontmatter && $0 == "---" { exit }
        frontmatter && index($0, key ":") == 1 {
            sub("^[^:]+:[[:space:]]*", "")
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

local_agent_assert_clean_worktree() {
    local root="$1"
    local dirty
    dirty=$(git -C "$root" status --porcelain=v1 --untracked-files=all)
    [ -z "$dirty" ] || {
        echo "FAIL: 로컬 에이전트 worktree에 커밋되지 않은 변경이 있다" >&2
        printf '%s\n' "$dirty" >&2
        return 1
    }
}

local_agent_assert_stage_paths() {
    local root="$1"
    local base="$2"
    local role="$3"
    local path
    local fail=0

    while IFS= read -r path; do
        [ -n "$path" ] || continue
        case "$role:$path" in
            test:src/main/*|test:*/src/main/*)
                echo "FAIL: Test 단계에서 production source 수정 금지: $path" >&2
                fail=1
                ;;
            feat:src/test/*|feat:*/src/test/*)
                echo "FAIL: Feat 단계에서 test source 수정 금지: $path" >&2
                fail=1
                ;;
            review:src/*|review:*/src/*)
                echo "FAIL: Review 단계에서 source 수정 금지: $path" >&2
                fail=1
                ;;
        esac
    done < <(git -C "$root" log --format= --name-only "$base..HEAD" | sort -u)

    [ "$fail" -eq 0 ]
}

local_agent_assert_review_report_schema() {
    local report="$1"
    local schema verdict restart

    schema=$(local_agent_extract_frontmatter_value "$report" schema)
    verdict=$(local_agent_extract_frontmatter_value "$report" verdict)
    restart=$(local_agent_extract_frontmatter_value "$report" restart_stage)

    [ "$schema" = "review-report/v1" ] || {
        echo "FAIL: Review frontmatter schema가 유효하지 않다: $schema" >&2
        return 1
    }
    case "$verdict:$restart" in
        approved:none|blocked:none|rejected:test|rejected:feat|rejected:refactor)
            ;;
        *)
            echo "FAIL: Review frontmatter 조합이 유효하지 않다: verdict=$verdict restart_stage=$restart" >&2
            return 1
            ;;
    esac
}

local_agent_assert_referenced_docs() {
    local report="$1"
    local allowed="$2"
    local path
    local count=0

    while IFS= read -r path; do
        [ -n "$path" ] || continue
        count=$((count + 1))
        grep -Fxq "$path" "$allowed" || {
            echo "FAIL: 보고서가 허용 목록 밖 문서를 참조했다: $path" >&2
            return 1
        }
    done < <(local_agent_extract_section_paths "$report" "## 실제 참조 문서")

    [ "$count" -gt 0 ] || {
        echo "FAIL: 보고서의 '실제 참조 문서' 목록이 비어 있다" >&2
        return 1
    }
}

local_agent_verify_project() {
    local root="$1"

    [ -x "$root/verify.sh" ] || {
        echo "BLOCKED: 프로젝트 green-bar 명령이 없다 또는 실행할 수 없다: $root/verify.sh" >&2
        return 2
    }
    "$root/verify.sh"
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
