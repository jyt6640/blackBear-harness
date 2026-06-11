#!/usr/bin/env bash
# 하네스 정합성 검증. 기계적으로 판정 가능한 규칙만 검사한다.
# 판단이 필요한 규칙은 md 지침으로 유지한다.
# → docs/decisions/accepted/enforcement-by-script.md
set -u
cd "$(git rev-parse --show-toplevel)"

fail=0
err() { echo "FAIL: $1"; fail=1; }

# 1. 상대 링크 무결성
while IFS= read -r line; do
    f="${line%%:*}"
    link="${line#*:}"
    dir=$(dirname "$f")
    target=$(python3 -c "import os; print(os.path.normpath(os.path.join('$dir','$link')))")
    [ -f "$target" ] || err "깨진 링크: $f → $link"
done < <(
    for f in AGENTS.md ARCHITECTURE.md $(find docs .claude/skills orchestrator sub-agent -name "*.md" 2>/dev/null); do
        grep -oE '\]\(\.{1,2}/[^)]+\.md\)' "$f" 2>/dev/null | sed "s|](\(.*\))|\1|" | while IFS= read -r l; do
            echo "$f:$l"
        done
    done
)

# 2. decision 상태와 디렉토리 일치 + 상태 섹션 존재
for dir in draft accepted rejected pending not-applicable superseded; do
    [ -d "docs/decisions/$dir" ] || continue
    for f in docs/decisions/"$dir"/*.md; do
        [ -e "$f" ] || continue
        status=$(awk '/^## 상태/{found=1; next} found && NF {print; exit}' "$f")
        if [ -z "$status" ]; then
            err "상태 섹션 없음: $f"
        elif [ "$status" != "$dir" ]; then
            err "상태($status) ≠ 디렉토리($dir): $f"
        fi
    done
done

# 3. 프로젝트 종속 누수 금지 패턴
if grep -rqiE "roomescape" docs AGENTS.md ARCHITECTURE.md 2>/dev/null; then
    err "프로젝트 종속 누수: $(grep -rliE 'roomescape' docs AGENTS.md ARCHITECTURE.md | tr '\n' ' ')"
fi

# 4. decisions의 프로젝트 기록 화법 재발 방지 (정본 화법: "이 하네스는")
if grep -rq "현재 프로젝트" docs/decisions 2>/dev/null; then
    err "decisions에 프로젝트 기록 화법: $(grep -rl '현재 프로젝트' docs/decisions | tr '\n' ' ')"
fi

# 5. QnA 초안 커밋 방지 (미확정 논의는 추적하지 않는다)
if git ls-files --error-unmatch PHILOSOPHY_QNA_DRAFT.md >/dev/null 2>&1; then
    err "PHILOSOPHY_QNA_DRAFT.md가 git에 추적되고 있다 (미확정 논의는 커밋하지 않는다)"
fi

# 6. AGENTS.md 필수 섹션 존재
for section in "# 작업 시퀀스" "# 판단 우선순위" "# 불변 철학" "# 기본 입장" "# 작업 유형별 필수 문서" "# 최종 점검"; do
    grep -q "^$section" AGENTS.md || err "AGENTS.md에 '$section' 섹션 없음"
done

if [ "$fail" -eq 0 ]; then
    echo "OK: 하네스 정합성 검증 통과"
fi
exit "$fail"
