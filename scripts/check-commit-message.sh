#!/usr/bin/env bash
# 커밋 메시지 형식 검사: type(scope): summary
# 사용: check-commit-message.sh <커밋메시지파일 또는 메시지 문자열>
# → docs/workflow/git-convention.md, docs/decisions/accepted/enforcement-by-script.md
set -u

input="${1:?사용법: check-commit-message.sh <메시지 파일 또는 문자열>}"
if [ -f "$input" ]; then
    subject=$(head -n1 "$input")
    body_src="$input"
else
    subject="$input"
    body_src=""
fi

# Co-Authored-By 트레일러 금지 (제목/본문 어디에도 허용하지 않는다)
if { [ -n "$body_src" ] && grep -qiE '^[[:space:]]*Co-Authored-By:' "$body_src"; } \
   || printf '%s' "$input" | grep -qiE 'Co-Authored-By:'; then
    echo "FAIL: 커밋 메시지에 Co-Authored-By 트레일러가 있다. 제거하고 다시 커밋한다."
    exit 1
fi

case "$subject" in
    Merge\ *|Revert\ *) exit 0 ;;
esac

if echo "$subject" | grep -qE '^(test|feat|fix|refactor|docs|chore)(\([a-zA-Z0-9._-]+\))?: .+'; then
    exit 0
fi

echo "FAIL: 커밋 제목이 'type(scope): summary' 형식이 아니다: $subject"
echo "      허용 type: test feat fix refactor docs chore"
exit 1
