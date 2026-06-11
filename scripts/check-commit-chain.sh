#!/usr/bin/env bash
# 카드 릴레이 커밋 체인 검사.
# 시작 ref 이후의 커밋이 아래를 지키는지 검사한다.
#   1) type(scope): summary 형식
#   2) 카드 릴레이 범위에는 test / feat / refactor type만
#   3) test → feat → refactor 순서 (역행 금지)
# 반려 후 재작업은 반려 시점 커밋을 새 시작 ref로 잡아 검사한다.
# 사용: check-commit-chain.sh <시작ref> [끝ref=HEAD]
set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE="${1:?사용법: check-commit-chain.sh <시작ref> [끝ref]}"
HEAD_REF="${2:-HEAD}"

fail=0
err() { echo "FAIL: $1"; fail=1; }

order_of() {
    case "$1" in
        test) echo 0 ;;
        feat) echo 1 ;;
        refactor) echo 2 ;;
        *) echo 9 ;;
    esac
}

stage=0
count=0
while IFS= read -r line; do
    [ -n "$line" ] || continue
    hash="${line%% *}"
    subject="${line#* }"
    count=$((count + 1))

    "$SCRIPT_DIR/check-commit-message.sh" "$subject" || fail=1

    type=$(printf '%s' "$subject" | sed -E 's/^([a-z]+).*/\1/')
    o=$(order_of "$type")
    if [ "$o" = "9" ]; then
        err "카드 릴레이 범위에는 test / feat / refactor 커밋만 허용된다: $hash $subject"
        continue
    fi
    if [ "$o" -lt "$stage" ]; then
        err "커밋 type 순서 위반 (test → feat → refactor): $hash $subject"
    else
        stage=$o
    fi
done < <(git log --reverse --no-merges --format='%h %s' "$BASE..$HEAD_REF")

[ "$count" -gt 0 ] || err "검사할 커밋이 없다: $BASE..$HEAD_REF"

if [ "$fail" -eq 0 ]; then
    echo "OK: 커밋 체인 검사 통과 (${count}개, $BASE..$HEAD_REF)"
fi
exit "$fail"
