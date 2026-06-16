#!/usr/bin/env bash
# 어댑터가 정본을 복제하지 않고 참조만 하는지 검사한다 (one canon, N adapters).
# → docs/decisions/accepted/cross-tool-adapter-layer.md
#
# 검사 원칙(스텁):
#  1. 각 어댑터가 존재하고 정본(AGENTS.md 또는 agents/<role>/AGENTS.md)을 가리키는가.
#  2. 어댑터가 규칙 본문을 복제하지 않았는가 (휴리스틱: 본문 줄 수 상한).
set -u
cd "$(git rev-parse --show-toplevel)"

fail=0
err() { echo "FAIL: $1"; fail=1; }

# 정본을 가리켜야 하는 루트 어댑터
declare -a ROOT_ADAPTERS=("CLAUDE.md" "GEMINI.md")
for a in "${ROOT_ADAPTERS[@]}"; do
    [ -f "$a" ] || { err "어댑터 없음: $a"; continue; }
    grep -q 'AGENTS.md' "$a" || err "$a 가 정본 AGENTS.md를 참조하지 않는다"
    # 본문 복제 휴리스틱: 어댑터는 얇아야 한다 (40줄 상한)
    lines=$(grep -cvE '^\s*$' "$a")
    [ "$lines" -le 40 ] || err "$a 가 너무 길다(${lines}줄) — 규칙 본문이 복제됐을 수 있다"
done

# Cursor 규칙: 정본 docs 경로를 참조해야 한다
for mdc in .cursor/rules/*.mdc; do
    [ -e "$mdc" ] || continue
    grep -qE 'AGENTS\.md|docs/|templates/|agents/|scripts/' "$mdc" \
        || err "$mdc 가 정본 경로를 참조하지 않는다"
    grep -q '정본이 아니다' "$mdc" || echo "WARN: $mdc 에 '정본이 아니다' 선언 권장"
done

# Claude 스킬 런처: 각 역할/메타 정본 agents/<name>/AGENTS.md를 가리켜야 한다
for role in test feat refactor review prompt-improver; do
    s=".claude/skills/$role/SKILL.md"
    [ -f "$s" ] || { err "스킬 런처 없음: $s"; continue; }
    grep -q "agents/$role/AGENTS.md" "$s" || err "$s 가 정본 agents/$role/AGENTS.md를 참조하지 않는다"
    lines=$(grep -cvE '^\s*$' "$s")
    [ "$lines" -le 30 ] || err "$s 가 너무 길다(${lines}줄) — 정본 복제 의심"
done

if [ "$fail" -eq 0 ]; then
    echo "OK: 어댑터가 정본을 참조만 한다 (복제 없음)"
fi
exit "$fail"
