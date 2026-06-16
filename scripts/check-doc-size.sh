#!/usr/bin/env bash
# 문서 크기/분리 운영 기준 검사.
# 공식 글자 수 제한이 아니다(벤더 제한 아님). backend-harness 운영 기준이다.
# → docs/decisions/accepted/document-size-and-splitting.md
#
# hard limit 위반 = FAIL (router/adapter/role 진입·연결 문서)
# soft limit 위반 = WARN (일반 docs — 검토 신호, verify를 실패시키지 않음)
set -u
cd "$(git rev-parse --show-toplevel)"

fail=0
warn=0
hard() { echo "FAIL(doc-size): $1"; fail=1; }
soft() { echo "WARN(doc-size): $1"; warn=$((warn+1)); }

lines() { [ -f "$1" ] && wc -l < "$1" | tr -d ' ' || echo 0; }
chars() { [ -f "$1" ] && wc -c < "$1" | tr -d ' ' || echo 0; }

# Index/목차 헤딩 또는 index 파일이면 면제
has_index() {
    case "$(basename "$1")" in *index.md) return 0 ;; esac
    grep -qiE '^#+ .*(index|목차|차례|contents)' "$1"
}

# ---- hard limit (FAIL) ----
hard_check() {
    local f="$1" limit="$2"
    [ -f "$f" ] || return 0
    local n; n=$(lines "$f")
    [ "$n" -le "$limit" ] || hard "$f: ${n}줄 > ${limit}줄 한도 (router/adapter/role은 짧아야 한다)"
}

hard_check AGENTS.md 150
hard_check CLAUDE.md 50
hard_check GEMINI.md 50
for f in agents/*/AGENTS.md; do hard_check "$f" 150; done
for f in .claude/skills/*/SKILL.md; do hard_check "$f" 80; done
for f in .cursor/rules/*.mdc; do hard_check "$f" 80; done

# ---- soft limit (WARN) ----
# 일반 docs: 글자 수 (architecture는 25k, 그 외 20k)
while IFS= read -r f; do
    [ -f "$f" ] || continue
    c=$(chars "$f"); n=$(lines "$f")
    case "$f" in
        docs/architecture/*) clim=25000 ;;
        *) clim=20000 ;;
    esac
    [ "$c" -le "$clim" ] || soft "$f: ${c}자 > ${clim}자 (의미 단위 분리 검토)"
    # >100줄 & Index 없음 — 단, decisions 와 index 파일은 면제(단일 주제)
    case "$f" in docs/decisions/*) continue ;; esac
    if [ "$n" -gt 100 ] && ! has_index "$f"; then
        soft "$f: ${n}줄, Index/목차 없음 (가이드 문서는 Index 추가 또는 분리 검토)"
    fi
done < <(find docs -name '*.md' 2>/dev/null)

# templates: 10k
for f in templates/*.md; do
    [ -f "$f" ] || continue
    c=$(chars "$f"); [ "$c" -le 10000 ] || soft "$f: ${c}자 > 10,000자 (템플릿 분리 검토)"
done

# references: 10KB
for f in docs/references/*-llms.txt; do
    [ -f "$f" ] || continue
    c=$(chars "$f"); [ "$c" -le 10240 ] || soft "$f: ${c}바이트 > 10KB (참조 압축/분리 검토)"
done

echo "---"
[ "$warn" -eq 0 ] || echo "WARN(doc-size) ${warn}건 (검토 권장, verify 실패 아님)"
if [ "$fail" -eq 0 ]; then
    echo "OK: 문서 크기 hard limit 통과"
fi
exit "$fail"
