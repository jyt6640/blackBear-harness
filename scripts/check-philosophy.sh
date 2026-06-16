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
caution() { echo "WARN:  $1"; warn=$((warn+1)); }
hard_violate() { echo "BLOCK: $1"; fail=1; }  # 모드 무관 강제 차단 (예: FQN)
base_violate() {
    if [ "${CHECK_PHILOSOPHY_BASE_STRICT:-0}" = "1" ]; then
        echo "BLOCK: $1"
        fail=1
    else
        caution "$1"
    fi
}
violate() { base_violate "$1"; }

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

# H2b. Controller에서 형식 검증 (regex/Pattern) — 형식 검증은 DTO로 — S1/S3
for f in $(files -name '*Controller.java'); do
    n=$(grep -nE '\.matches\(|Pattern\.|isValidEmail|\.isBlank\(\)' "$f" | head -1 | cut -d: -f1)
    [ -n "$n" ] && violate "Controller에서 형식 검증 ($f:$n) — 형식 검증은 Request DTO(@NotNull/@NotBlank/@Pattern) 또는 값 객체로"
done

# H2c. Controller에서 에러 응답 조립 — 에러 변환은 global 핸들러로 — S1/S7
# 에러 봉투 키("code"/"errors")는 global 핸들러 전용. 성공 응답은 "data"를 쓴다.
for f in $(files -name '*Controller.java'); do
    n=$(grep -nE '"(code|errors)"' "$f" | head -1 | cut -d: -f1)
    [ -n "$n" ] && violate "Controller에서 에러 응답 조립 ($f:$n) — 에러 봉투(code/errors)는 예외를 던져 global 핸들러가 만든다"
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

# Soft. Controller의 ResponseEntity<?> 와일드카드 (약타입 — 응답 타입 명시 권장) — 검토 신호
for f in $(files -name '*Controller.java'); do
    while IFS=: read -r n line; do
        [ -n "$n" ] && caution "Controller 와일드카드 응답 ($f:$n) — 응답 타입을 명시하라 (프로젝트가 봉투 타입을 쓰면 그 타입으로)"
    done < <(grep -nE 'ResponseEntity<\?>' "$f" 2>/dev/null)
done

# Soft. else 블록 (early return 선호) — R2, 차단하지 않고 검토 신호
while IFS=: read -r f n line; do
    caution "else 블록 ($f:$n) — early return/throw로 풀 수 있는지 검토"
done < <(grep -rnE '\}[[:space:]]*else[[:space:]]*\{|^[[:space:]]*else[[:space:]]*\{' "$SRC" 2>/dev/null)

# Hard. 인라인 FQN (import 안 하고 본문에 완전수식명) — 강제 차단(BLOCK). import해 simple name 사용.
# import/package 선언 줄은 제외하고, 본문에서 a.b.c.Type 형태(소문자 세그먼트 2+ 뒤 대문자 타입)를 찾는다.
while IFS=: read -r f n line; do
    case "$line" in *import\ *|*package\ *) continue ;; esac
    printf '%s' "$line" | grep -qE '^[[:space:]]*(\*|//|/\*)' && continue
    printf '%s' "$line" | grep -q '{@link' && continue
    hard_violate "인라인 FQN ($f:$n) — 타입을 import해 simple name으로 쓴다: $(echo "$line"|sed 's/^[[:space:]]*//' | cut -c1-70)"
done < <(grep -rnE '([a-z][a-z0-9_]*\.){2,}[A-Z][A-Za-z0-9_]*' "$SRC" 2>/dev/null)

# 프로젝트 전용 검사 훅은 hard block이다. base 검사는 기본 WARN이고 프로젝트가
# CHECK_PHILOSOPHY_BASE_STRICT=1을 설정한 경우에만 hard block으로 승격한다.
if [ -f "$ROOT/scripts/check-philosophy.project.sh" ]; then
    violate() { echo "BLOCK: $1"; fail=1; }
    # shellcheck source=/dev/null
    . "$ROOT/scripts/check-philosophy.project.sh"
fi

echo "---"
[ "$warn" -eq 0 ] || echo "WARN ${warn}건 (차단 안 함, 검토 권장)"
if [ "$fail" -eq 0 ]; then
    echo "OK: 기계 판정 철학 위반 없음 ($SRC)"
fi
exit "$fail"
