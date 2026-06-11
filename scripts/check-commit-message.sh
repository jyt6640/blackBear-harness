#!/usr/bin/env bash
# 커밋 메시지 형식 검사: type(scope): summary
# 사용: check-commit-message.sh <커밋메시지파일 또는 메시지 문자열>
# → docs/workflow/git-convention.md, docs/decisions/accepted/enforcement-by-script.md
set -u

input="${1:?사용법: check-commit-message.sh <메시지 파일 또는 문자열>}"
if [ -f "$input" ]; then
    subject=$(head -n1 "$input")
else
    subject="$input"
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
