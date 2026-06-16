#!/usr/bin/env bash
# 커밋 전 빠른 하네스 검사. 전체 링크/decision/history 검사는 verify-harness.sh가 맡는다.
set -u
cd "$(git rev-parse --show-toplevel)"

fail=0
err() { echo "FAIL: $1"; fail=1; }

git diff --cached --check || fail=1

if git ls-files --error-unmatch PHILOSOPHY_QNA_DRAFT.md >/dev/null 2>&1; then
    err "PHILOSOPHY_QNA_DRAFT.md가 git에 추적되고 있다"
fi

if git ls-files 'next-step/work/*' | grep -q .; then
    err "next-step/work 아래 파일이 git에 추적되고 있다"
fi

for script in scripts/verify-harness.sh scripts/check-philosophy.sh scripts/local-agent/run-stage.sh; do
    [ -x "$script" ] || err "필수 스크립트가 없거나 실행할 수 없다: $script"
done

[ "$fail" -ne 0 ] || echo "OK: 빠른 하네스 검사 통과"
exit "$fail"
