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
    for f in AGENTS.md ARCHITECTURE.md README.md $(find docs agents next-step .claude/skills templates -name "*.md" 2>/dev/null); do
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
if grep -rqiE "roomescape" docs agents next-step AGENTS.md ARCHITECTURE.md 2>/dev/null; then
    err "프로젝트 종속 누수: $(grep -rliE 'roomescape' docs agents next-step AGENTS.md ARCHITECTURE.md | tr '\n' ' ')"
fi

# 4. decisions의 프로젝트 기록 화법 재발 방지 (정본 화법: "이 하네스는")
if grep -rq "현재 프로젝트" docs/decisions 2>/dev/null; then
    err "decisions에 프로젝트 기록 화법: $(grep -rl '현재 프로젝트' docs/decisions | tr '\n' ' ')"
fi

# 5. QnA 초안 커밋 방지 (미확정 논의는 추적하지 않는다)
if git ls-files --error-unmatch PHILOSOPHY_QNA_DRAFT.md >/dev/null 2>&1; then
    err "PHILOSOPHY_QNA_DRAFT.md가 git에 추적되고 있다 (미확정 논의는 커밋하지 않는다)"
fi

# 6. AGENTS.md 필수 섹션 존재 (라우터: 규칙 본문은 정본으로 이주, 여기서는 가리킨다)
for section in "# 문서 지도" "# 오케스트레이터 책임" "# 작업 시퀀스" "# 산출물 체인" "# 실행 모드" "# 판단 우선순위" "# 불변 철학" "# 정본과 어댑터"; do
    grep -q "^$section" AGENTS.md || err "AGENTS.md에 '$section' 섹션 없음"
done

# 6b. AGENTS.md 150줄 이하 (라우터 유지) → docs/decisions/accepted/cross-tool-adapter-layer.md
agents_lines=$(wc -l < AGENTS.md | tr -d ' ')
[ "$agents_lines" -le 150 ] || err "AGENTS.md가 150줄을 초과한다(${agents_lines}줄) — 라우터로 유지하라"

# 6c. 어댑터 정합성 (one canon, N adapters)
if [ -x scripts/check-adapter-sync.sh ]; then
    scripts/check-adapter-sync.sh >/dev/null 2>&1 || err "어댑터 정합성 실패 (scripts/check-adapter-sync.sh)"
fi

# 7. 역할별 AGENTS.md 필수 섹션과 docs 존재
for role in test feat refactor review; do
    f="agents/$role/AGENTS.md"
    [ -f "$f" ] || { err "역할 AGENTS.md 없음: $f"; continue; }
    for section in "## 책임" "## 입력" "## 출력" "## 금지" "## 완료 기준"; do
        grep -q "^$section" "$f" || err "$f에 '$section' 섹션 없음"
    done
    # 철학 단일 출처(C8 환원): 역할 정본은 철학을 재서술하지 않고 docs/principles 정본을 참조한다.
    grep -q 'docs/principles/' "$f" || err "$f가 docs/principles 정본을 참조하지 않는다 (철학은 정본 링크로, 재서술 금지)"
    # 역할 루프는 단일 출처(loop-engineering)를 참조한다.
    grep -q 'loop-engineering' "$f" || err "$f에 역할 루프(단일 출처 loop-engineering) 참조 없음"
    if ! find "agents/$role/docs" -maxdepth 1 -name "*.md" -type f 2>/dev/null | grep -q .; then
        err "역할 docs 없음: agents/$role/docs"
    fi
    # 역할 docs/는 실행 체크리스트·절차만 둔다(철학 요약 계약은 폐지). 철학 정본은 docs/principles다.
    [ -f "agents/$role/docs/workflow.md" ] || err "역할 workflow 문서 없음: agents/$role/docs/workflow.md"
    script="agents/$role/scripts/enforce-workflow.sh"
    [ -x "$script" ] || err "역할 workflow 강제 스크립트 없음 또는 실행 불가: $script"
done

# 7b. prompt-improver 메타 역할 정본 존재 (TDD 릴레이 단계 아님 — docs/scripts 계약 면제)
[ -f "agents/prompt-improver/AGENTS.md" ] || err "prompt-improver 역할 정본 없음: agents/prompt-improver/AGENTS.md"
grep -q 'proposal-only\|proposal only\|승인 후' agents/prompt-improver/AGENTS.md 2>/dev/null \
    || err "agents/prompt-improver/AGENTS.md에 proposal-only 적용 정책 없음"

# 8. 작업 메모리 추적 방지
if git ls-files 'next-step/work/*' | grep -q .; then
    err "next-step/work 아래 파일이 git에 추적되고 있다"
fi

grep -q '^next-step/work/$' .gitignore || err ".gitignore에 next-step/work/ 없음"
[ -f "next-step/history/README.md" ] || err "next-step/history/README.md 없음"

# 완료 기록(history)의 카드는 Review 판정과 철학 점수표를 모두 포함해야 한다
for h in next-step/history/*/; do
    [ -d "$h" ] || continue
    if [ -f "${h}04-review-report.md" ] && [ ! -f "${h}06-scorecard.md" ]; then
        err "history 카드에 06-scorecard.md 없음 (채점 없이 완료 기록 금지): $h"
    fi
done

# 9. next-step 템플릿 존재
for template in backlog.md 00-task-card.md 01-red-test-report.md 02-green-implementation-report.md 03-refactor-report.md 04-review-report.md 05-summary.md 06-scorecard.md; do
    [ -f "templates/$template" ] || err "next-step 템플릿 없음: templates/$template"
done

# 10. 자동 로컬 에이전트 입력 계약
for section in "## 필수 상위 문서" "## 역할별 추가 문서" "### Test" "### Feat" "### Refactor" "### Review"; do
    grep -q "^$section" templates/00-task-card.md || err "00-task-card.md에 '$section' 섹션 없음"
done
grep -q '^## 재실행 단계' templates/04-review-report.md || err "04-review-report.md에 재실행 단계 섹션 없음"
grep -q '^schema: review-report/v1$' templates/04-review-report.md || err "04-review-report.md에 review-report/v1 schema 없음"
for template in 01-red-test-report.md 02-green-implementation-report.md 03-refactor-report.md 04-review-report.md; do
    grep -q '^## 실제 참조 문서' "templates/$template" \
        || err "$template에 '실제 참조 문서' 섹션 없음"
done

# 점수표 항목과 채점 기준의 ID 일치
for iid in $(grep -oE '^\| [A-Z][0-9]+' templates/06-scorecard.md | tr -d '| '); do
    grep -q "^## $iid\." docs/workflow/loop-scoring-criteria.md || err "채점 기준 없음: $iid (loop-scoring-criteria.md)"
done

# 11. 필수 스킬 존재 (얇은 런처)
for skill in orchestrate test feat refactor review local-agent loop-improve prompt-improver draft-decision harness-interview harness-sync; do
    [ -f ".claude/skills/$skill/SKILL.md" ] || err "필수 스킬 없음: .claude/skills/$skill/SKILL.md"
done

# 12. 강제 스크립트와 훅 존재 / 실행 권한
for sc in \
    scripts/check-commit-message.sh \
    scripts/check-commit-chain.sh \
    scripts/local-agent/lib.sh \
    scripts/local-agent/check-provider.sh \
    scripts/local-agent/run-stage.sh \
    scripts/local-agent/run-pipeline.sh \
    scripts/local-agent/test-runner.sh \
    scripts/loop/aggregate-scores.sh \
    scripts/check-artifact-chain.sh \
    scripts/check-task-card.sh \
    scripts/check-philosophy.sh \
    scripts/check-doc-size.sh \
    scripts/verify-fast.sh \
    verify.sh \
    scripts/local-agent/run-backlog.sh \
    scripts/project-templates/verify.sh \
    .githooks/pre-commit \
    .githooks/commit-msg
do
    [ -x "$sc" ] || err "강제 스크립트 없음 또는 실행 권한 없음: $sc"
done

# 13. 문서 크기/분리 기준 (hard=FAIL, soft=WARN)
if [ -x scripts/check-doc-size.sh ]; then
    scripts/check-doc-size.sh || err "문서 크기 hard limit 위반 (scripts/check-doc-size.sh)"
fi

if [ "$fail" -eq 0 ]; then
    echo "OK: 하네스 정합성 검증 통과"
fi
exit "$fail"
