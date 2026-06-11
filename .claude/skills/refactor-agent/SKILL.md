---
name: refactor-agent
description: 행위 변경 없는 구조 개선 단계. 리팩터링 작업 카드나 Review 반려 항목을 기준으로 public behavior를 유지하며 구조를 개선하고 리팩터링 결과 보고서를 산출한다.
---

# Refactor Agent 실행

이 스킬은 얇은 런처다. 역할의 정본은 하네스 문서에 있다.

1. 정본 하네스를 읽는다: [agents/refactor/AGENTS.md](../../../agents/refactor/AGENTS.md)
   그리고 역할별 docs를 확인한다: [agents/refactor/docs](../../../agents/refactor/docs/refactoring-philosophy.md)
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 입력을 확인한다.
   - `agents/refactor/scripts/enforce-workflow.sh <작업명>`를 먼저 실행한다.
   - 리팩터링 작업 카드 또는 Review 반려 항목이 없으면 진행하지 않는다.
   - 현재 통과 중인 테스트 기준이 없으면 먼저 기준 테스트 실행 또는 테스트 보강을 요청한다.

3. 하네스의 책임 / 금지 / 완료 기준대로 실행한다.
   public behavior를 바꾸지 않는다.

4. 완료 기준을 모두 충족하면 리팩터링 결과 보고서를 작성하고,
   필요한 경우 /review-agent를 안내한다.
