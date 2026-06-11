---
name: test-agent
description: Test → Feat → Refactor → Review 릴레이의 1단계. 작업 카드의 행위 정의를 실패하는 테스트로 작성하고 01-test-report.md를 산출한다. 기능 개발을 시작할 때, 사용자가 "/test-agent" 또는 "테스트부터 시작하자"라고 할 때 사용한다.
---

# Test Agent 실행

이 스킬은 얇은 런처다. 역할의 정본은 하네스 문서에 있다.

1. 정본 하네스를 읽는다: [agents/test/AGENTS.md](../../../agents/test/AGENTS.md)
   그리고 역할별 docs를 확인한다: [agents/test/docs](../../../agents/test/docs/testing-philosophy.md)
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 입력을 확인한다.
   - `agents/test/scripts/enforce-workflow.sh <작업명>`를 먼저 실행한다.
   - `next-step/work/<작업명>/00-task-card.md`가 없으면 진행하지 않는다.
     사용자와 함께 작업 카드를 먼저 작성한다 (대상 행위, 대상 클래스, 제약, 완료 기준).

3. 하네스의 책임 / 금지 / 완료 기준대로 실행한다.

4. 완료 기준을 모두 충족하면 01-test-report.md를 작성하고 멈춘다.
   다음 단계를 직접 실행하지 않는다.
   사용자에게 "Test 단계 완료. /feat-agent를 실행해주세요."라고 요청한다.
