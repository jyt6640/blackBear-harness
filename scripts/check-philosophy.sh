#!/usr/bin/env bash
# 기계 판정 가능한 철학 위반을 사전 차단한다.
# 약한 모델이 자주 어기는 '명백한' 위반을 리뷰 전 Feat/Refactor 게이트에서 막는다.
# 코드 의미 판단이 필요한 위반(Service에 비즈니스 판단 등)은 점수표 루프의 몫이다.
# → docs/decisions/accepted/mechanical-philosophy-block.md
#
# 사용: scripts/check-philosophy.sh [src경로]  (기본 src/main/java)
# Java 소스가 없으면 no-op (base 하네스 저장소에서는 통과).
set -u

ROOT="$(git rev-parse --show-toplevel)"
SRC="${1:-src/main/java}"
cd "$ROOT"
[ -d "$SRC" ] || { echo "INFO: Java 소스 디렉토리 없음($SRC) — 철학 검사 생략"; exit 0; }

fail=0
warn=0
violate() { echo "BLOCK: $1"; fail=1; }
caution() { echo "WARN:  $1"; warn=$((warn+1)); }

files() { find "$SRC" -name '*.java' "$@" 2>/dev/null; }

# H1. Util / Helper / Manager 클래스 (책임 불명 이름) — R1
while IFS=: read -r f n line; do
    [ -n "$f" ] && violate "책임 불명 클래스명 ($f:$n): ${line#*:}"
done < <(grep -rnE '(class|interface)[[:space:]]+[A-Za-z0-9_]*(Util|Helper|Manager)\b' "$SRC" 2>/dev/null)

# H2. Controller try-catch (예외는 GlobalExceptionHandler로) — S1/S7
for f in $(files -name '*Controller.java'); do
    n=$(grep -nE '\bcatch[[:space:]]*\(' "$f" | head -1 | cut -d: -f1)
    [ -n "$n" ] && violate "Controller에서 try-catch ($f:$n) — 예외는 global 핸들러로"
done

# H3. Domain 패키지에 setter — S5
while IFS=: read -r f n line; do
    case "$f" in */domain/*) violate "Domain에 setter ($f:$n): $(echo "$line"|sed 's/^[[:space:]]*//')" ;; esac
done < <(grep -rnE 'public[[:space:]]+void[[:space:]]+set[A-Z][A-Za-z0-9_]*[[:space:]]*\(' "$SRC" 2>/dev/null)

# H4. 클래스 내부 중첩 클래스 (들여쓴 class/record/enum 선언) — 구조
while IFS=: read -r f n line; do
    case "$line" in *@*) continue ;; esac
    violate "중첩 클래스 ($f:$n): $(echo "$line"|sed 's/^[[:space:]]*//')"
done < <(grep -rnE '^[[:space:]]+(public |private |protected |static |final |abstract |sealed )*(class|record|enum|interface)[[:space:]]+[A-Z]' "$SRC" 2>/dev/null)

# H5. 도메인별 예외 핸들러 (global이 아닌 곳의 @ControllerAdvice/@ExceptionHandler) — S7
while IFS=: read -r f n line; do
    case "$f" in */global/*) ;; *) violate "global 밖 예외 핸들러 ($f:$n) — 핸들러는 global에 둔다" ;; esac
done < <(grep -rnE '@(Rest)?ControllerAdvice|@ExceptionHandler' "$SRC" 2>/dev/null)

# H6. DTO가 dto 패키지 밖 (Request/Response/Command/Query 클래스) — 패키지 구조
while IFS=: read -r f n line; do
    case "$f" in
        */dto/*) ;;
        *) violate "DTO가 dto 패키지 밖 ($f:$n): $(echo "$line"|sed 's/^[[:space:]]*//' | cut -c1-60)" ;;
    esac
done < <(grep -rnE '(class|record)[[:space:]]+[A-Za-z0-9_]+(Request|Response|Command|Query)\b' "$SRC" 2>/dev/null)

# H7. throw new RuntimeException (도메인 의미 예외 사용) — S7
while IFS=: read -r f n line; do
    violate "throw new RuntimeException ($f:$n) — 도메인 의미 예외(ErrorCode)를 던진다"
done < <(grep -rnE 'throw[[:space:]]+new[[:space:]]+RuntimeException[[:space:]]*\(' "$SRC" 2>/dev/null)

# H8. 삼항 연산자 (return/대입의 ?: — early return/throw로) — R2
while IFS=: read -r f n line; do
    case "$line" in *'<?'*|*'?>'*) continue ;; esac
    violate "삼항 연산자 ($f:$n): $(echo "$line"|sed 's/^[[:space:]]*//' | cut -c1-60)"
done < <(grep -rnE '(return|=)[^;]*\?[^;:?]+:[^;]+;' "$SRC" 2>/dev/null)

# Soft. else 블록 (early return 선호) — R2, 차단하지 않고 검토 신호
while IFS=: read -r f n line; do
    caution "else 블록 ($f:$n) — early return/throw로 풀 수 있는지 검토"
done < <(grep -rnE '\}[[:space:]]*else[[:space:]]*\{|^[[:space:]]*else[[:space:]]*\{' "$SRC" 2>/dev/null)

echo "---"
[ "$warn" -eq 0 ] || echo "WARN $warn건 (차단 안 함, 검토 권장)"
if [ "$fail" -eq 0 ]; then
    echo "OK: 기계 판정 철학 위반 없음 ($SRC)"
fi
exit "$fail"
