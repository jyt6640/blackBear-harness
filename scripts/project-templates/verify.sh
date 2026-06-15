#!/usr/bin/env bash
# 프로젝트 green-bar: build + test가 통과하면 0, 아니면 비0.
# 이 프로젝트에서 "되는 코드"의 단일 기계적 정의다.
# 역할 에이전트(Feat/Refactor)와 백로그 루프가 이 한 명령으로 완료를 판정한다.
# 모델이 무엇이든(로컬 LLM / Claude) 같은 자로 잰다.
#
# 사용: 프로젝트 저장소 루트에 verify.sh로 복사하고 빌드 도구에 맞게 채운다.
# → docs/decisions/accepted/project-verify-green-bar.md
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"

# 예시 (프로젝트 빌드 도구에 맞게 교체):
#   Gradle:  ./gradlew clean build
#   Gradle(테스트만 빠르게): ./gradlew test
#   Maven:   ./mvnw -q verify
#
# 아래를 프로젝트 명령으로 교체한다.
echo "FAIL: verify.sh를 프로젝트 build+test 명령으로 채워라" >&2
exit 1
