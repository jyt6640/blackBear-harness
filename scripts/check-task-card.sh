#!/usr/bin/env bash
# 작업 카드의 '이번 작업 컴파일 규칙' 검증.
# 게이트는 파일 존재만 보고 내용 적용은 못 보지만, 오케스트레이터가
# 상위 docs 규칙을 카드별로 컴파일했는지(=실행자가 따를 구속 계약)는 검증할 수 있다.
# → docs/workflow/loop-engineering.md, docs/decisions/accepted/enforcement-by-script.md
set -u

ROOT="$(git rev-parse --show-toplevel)"
TASK="${1:?사용법: check-task-card.sh <작업명>}"
CARD="$ROOT/next-step/work/$TASK/00-task-card.md"

fail=0
err() { echo "FAIL: $1"; fail=1; }

[ -f "$CARD" ] || { echo "FAIL: 작업 카드가 없다: $CARD" >&2; exit 1; }

section="$(awk '/^## 이번 작업 컴파일 규칙/{f=1; next} f && /^## /{exit} f{print}' "$CARD")"

[ -n "$(printf '%s' "$section" | tr -d '[:space:]')" ] || err "'이번 작업 컴파일 규칙' 섹션이 비어 있다"

if printf '%s\n' "$section" | grep -q "컴파일 필요"; then
    err "컴파일 규칙이 템플릿 placeholder 그대로다 (오케스트레이터가 이번 작업 규칙으로 컴파일해야 한다)"
fi

cited="$(printf '%s\n' "$section" | grep -cE '^- .*\(.+\)' || true)"
if [ "$cited" -lt 2 ]; then
    err "출처를 인용한 컴파일 규칙이 2개 이상이어야 한다 (현재 ${cited}). 각 줄 끝에 (출처 decision/문서)를 적는다"
fi

if [ "$fail" -eq 0 ]; then
    echo "OK: 작업 카드 컴파일 규칙 검증 통과 ($TASK)"
fi
exit "$fail"
