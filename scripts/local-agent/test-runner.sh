#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=lib.sh
source "$SCRIPT_DIR/lib.sh"
ROOT="$(git rev-parse --show-toplevel)"
TASK="local-agent-runner-test-$$"
WORK="$ROOT/next-step/work/$TASK"
TMP_ROOT="$(mktemp -d)"

cleanup() {
    rm -rf "$WORK"
    rm -rf "$TMP_ROOT"
}
trap cleanup EXIT

fail() {
    echo "FAIL: $1" >&2
    exit 1
}

mkdir -p "$WORK"
cat > "$WORK/00-task-card.md" <<EOF
# 00 Task Card

## 작업명

- $TASK

## 시작 기준 commit

- $(git -C "$ROOT" rev-parse HEAD)

## 이번 작업 컴파일 규칙

- 기계 판정 가능한 실행기 계약은 shell 테스트로 강제한다 (enforcement-by-script).
- 역할 입력은 작업 카드에 컴파일된 문서로 제한한다 (task-card-compiles-rules).

## 필수 상위 문서

- \`docs/decisions/accepted/agent-handoff-by-artifact.md\`

## 역할별 추가 문서

### Test

- \`docs/workflow/tdd.md\`

### Feat

- \`docs/architecture/layered-architecture.md\`

### Refactor

- \`docs/workflow/refactoring.md\`

### Review

- \`docs/workflow/code-review.md\`
EOF

OUTPUT="$("$SCRIPT_DIR/run-stage.sh" test "$TASK" --profile unused --dry-run)"
printf '%s\n' "$OUTPUT" | grep -q 'agents/test/AGENTS.md' \
    || fail "Test 역할 하네스가 dry-run 입력에 없다"
printf '%s\n' "$OUTPUT" | grep -q 'docs/decisions/accepted/agent-handoff-by-artifact.md' \
    || fail "공통 상위 문서가 dry-run 입력에 없다"
printf '%s\n' "$OUTPUT" | grep -q 'docs/workflow/tdd.md' \
    || fail "Test 추가 문서가 dry-run 입력에 없다"
if printf '%s\n' "$OUTPUT" | grep -q 'docs/architecture/layered-architecture.md'; then
    fail "Feat 전용 문서가 Test 입력에 섞였다"
fi

if "$SCRIPT_DIR/run-stage.sh" unknown "$TASK" --profile unused --dry-run >/dev/null 2>&1; then
    fail "지원하지 않는 역할을 허용했다"
fi

if "$SCRIPT_DIR/run-stage.sh" test missing-task --profile unused --dry-run >/dev/null 2>&1; then
    fail "작업 카드 없이 dry-run을 허용했다"
fi

if "$SCRIPT_DIR/run-stage.sh" test ../outside --profile unused --dry-run >/dev/null 2>&1; then
    fail "경로 구분자가 있는 작업명을 허용했다"
fi
if "$SCRIPT_DIR/run-stage.sh" test "unsafe task" --profile unused --dry-run >/dev/null 2>&1; then
    fail "공백이 있는 작업명을 허용했다"
fi
if "$SCRIPT_DIR/run-stage.sh" test "-unsafe" --profile unused --dry-run >/dev/null 2>&1; then
    fail "영문/숫자로 시작하지 않는 작업명을 허용했다"
fi

perl -0pi -e \
    's/## 역할별 추가 문서/- `docs\/does-not-exist.md`\n\n## 역할별 추가 문서/' \
    "$WORK/00-task-card.md"
if "$SCRIPT_DIR/run-stage.sh" test "$TASK" --profile unused --dry-run >/dev/null 2>&1; then
    fail "존재하지 않는 상위 문서를 허용했다"
fi

GIT_ROOT="$TMP_ROOT/git"
mkdir -p "$GIT_ROOT"
git -C "$GIT_ROOT" init -q
git -C "$GIT_ROOT" config user.name "Local Agent Test"
git -C "$GIT_ROOT" config user.email "local-agent@example.com"
printf 'tracked\n' > "$GIT_ROOT/tracked.txt"
printf 'ignored/\n' > "$GIT_ROOT/.gitignore"
git -C "$GIT_ROOT" add .gitignore tracked.txt
git -C "$GIT_ROOT" commit -qm "test: fixture"

local_agent_assert_clean_worktree "$GIT_ROOT" \
    || fail "clean 저장소를 dirty로 판정했다"

mkdir -p "$GIT_ROOT/ignored"
printf 'ignored\n' > "$GIT_ROOT/ignored/file.txt"
local_agent_assert_clean_worktree "$GIT_ROOT" \
    || fail "ignored 파일을 dirty로 판정했다"

printf 'untracked\n' > "$GIT_ROOT/untracked.txt"
if local_agent_assert_clean_worktree "$GIT_ROOT" >/dev/null 2>&1; then
    fail "untracked 파일을 허용했다"
fi
rm "$GIT_ROOT/untracked.txt"

printf 'changed\n' >> "$GIT_ROOT/tracked.txt"
if local_agent_assert_clean_worktree "$GIT_ROOT" >/dev/null 2>&1; then
    fail "unstaged tracked 변경을 허용했다"
fi
git -C "$GIT_ROOT" restore tracked.txt

printf 'staged\n' >> "$GIT_ROOT/tracked.txt"
git -C "$GIT_ROOT" add tracked.txt
if local_agent_assert_clean_worktree "$GIT_ROOT" >/dev/null 2>&1; then
    fail "staged 변경을 허용했다"
fi
git -C "$GIT_ROOT" restore --staged tracked.txt
git -C "$GIT_ROOT" restore tracked.txt

mkdir -p "$GIT_ROOT/src/main/java" "$GIT_ROOT/src/test/java"
printf 'main\n' > "$GIT_ROOT/src/main/java/Main.java"
printf 'test\n' > "$GIT_ROOT/src/test/java/MainTest.java"
git -C "$GIT_ROOT" add src
git -C "$GIT_ROOT" commit -qm "test: sources"
BASE="$(git -C "$GIT_ROOT" rev-parse HEAD)"

printf 'changed\n' >> "$GIT_ROOT/src/main/java/Main.java"
git -C "$GIT_ROOT" add src/main/java/Main.java
git -C "$GIT_ROOT" commit -qm "test: production violation"
if local_agent_assert_stage_paths "$GIT_ROOT" "$BASE" test >/dev/null 2>&1; then
    fail "Test 단계의 production source 변경을 허용했다"
fi

git -C "$GIT_ROOT" reset --hard -q "$BASE"
printf 'changed\n' >> "$GIT_ROOT/src/test/java/MainTest.java"
git -C "$GIT_ROOT" add src/test/java/MainTest.java
git -C "$GIT_ROOT" commit -qm "feat: test violation"
if local_agent_assert_stage_paths "$GIT_ROOT" "$BASE" feat >/dev/null 2>&1; then
    fail "Feat 단계의 test source 변경을 허용했다"
fi

APPROVED="$TMP_ROOT/approved.md"
cat > "$APPROVED" <<'EOF'
---
schema: review-report/v1
verdict: approved
restart_stage: none
---
EOF
local_agent_assert_review_report_schema "$APPROVED" \
    || fail "유효한 Review frontmatter를 거부했다"

INVALID="$TMP_ROOT/invalid.md"
cat > "$INVALID" <<'EOF'
---
schema: review-report/v1
verdict: approved
restart_stage: feat
---
EOF
if local_agent_assert_review_report_schema "$INVALID" >/dev/null 2>&1; then
    fail "유효하지 않은 Review frontmatter 조합을 허용했다"
fi

INVALID_SCHEMA="$TMP_ROOT/invalid-schema.md"
cat > "$INVALID_SCHEMA" <<'EOF'
---
schema: review-report/v2
verdict: approved
restart_stage: none
---
EOF
if local_agent_assert_review_report_schema "$INVALID_SCHEMA" >/dev/null 2>&1; then
    fail "알 수 없는 Review schema를 허용했다"
fi

ALLOWED="$TMP_ROOT/allowed-docs"
printf '%s\n' \
    "agents/test/AGENTS.md" \
    "next-step/work/$TASK/00-task-card.md" > "$ALLOWED"
REFERENCES="$TMP_ROOT/references.md"
cat > "$REFERENCES" <<EOF
## 실제 참조 문서

- \`agents/test/AGENTS.md\`
- \`next-step/work/$TASK/00-task-card.md\`
EOF
local_agent_assert_referenced_docs "$REFERENCES" "$ALLOWED" \
    || fail "허용 목록 안의 실제 참조 문서를 거부했다"
printf '%s\n' '- `docs/outside.md`' >> "$REFERENCES"
if local_agent_assert_referenced_docs "$REFERENCES" "$ALLOWED" >/dev/null 2>&1; then
    fail "허용 목록 밖 실제 참조 문서를 허용했다"
fi

PHILOSOPHY_SRC="$WORK/philosophy-src"
mkdir -p "$PHILOSOPHY_SRC"
cat > "$PHILOSOPHY_SRC/ExampleUtil.java" <<'EOF'
class ExampleUtil {
}
EOF
"$ROOT/scripts/check-philosophy.sh" "${PHILOSOPHY_SRC#$ROOT/}" >/dev/null \
    || fail "base 철학 quick check가 기본 모드에서 hard block됐다"
if CHECK_PHILOSOPHY_BASE_STRICT=1 \
    "$ROOT/scripts/check-philosophy.sh" "${PHILOSOPHY_SRC#$ROOT/}" >/dev/null 2>&1; then
    fail "strict base 철학 검사가 위반을 허용했다"
fi

echo "OK: local agent runner tests passed"
