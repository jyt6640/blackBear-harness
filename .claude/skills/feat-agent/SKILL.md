---
name: feat-agent
description: Test → Feat → Refactor → Review 릴레이의 2단계. 01-test-report의 실패 테스트를 최소 구현으로 통과시키고 02-implementation-report.md를 산출한다. 테스트 작성이 끝났을 때, 사용자가 "/feat-agent" 또는 "구현하자"라고 할 때 사용한다.
---

# Feat Agent 실행

이 스킬은 얇은 런처다. 역할의 정본은 하네스 문서에 있다.

1. 정본 하네스를 읽는다: [agents/feat/AGENTS.md](../../../agents/feat/AGENTS.md)
   그리고 역할별 docs를 확인한다: [agents/feat/docs](../../../agents/feat/docs/implementation-philosophy.md)
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 입력을 확인한다.
   - `agents/feat/scripts/enforce-workflow.sh <작업명>`를 먼저 실행한다.
   - `next-step/work/<작업명>/01-test-report.md`가 없으면 진행을 거부한다.
     테스트 단계가 먼저다. /test-agent를 안내한다.

3. 작업 카드의 `이번 작업 컴파일 규칙`을 먼저 읽는다. 이번 작업의 1차 구속 계약이다.
   하네스의 책임 / 금지 / 완료 기준대로 실행하고, 카드 규칙이 모호할 때만 출처 정본을 확인한다.
   테스트는 절대 수정하지 않는다. 테스트가 잘못됐으면 반려 사유와 함께 멈춘다.

4. 완료 기준을 모두 충족하면 02-implementation-report.md를 작성하고 멈춘다.
   다음 단계를 직접 실행하지 않는다.
   사용자에게 "Feat 단계 완료. /refactor-agent를 실행해주세요."라고 요청한다.
