---
name: feat-agent
description: Test → Feat → Review 사이클의 2단계. 01-test-report의 실패 테스트를 최소 구현으로 통과시키고 02-implementation-report.md를 산출한다. 테스트 작성이 끝났을 때, 사용자가 "/feat-agent" 또는 "구현하자"라고 할 때 사용한다.
---

# Backend Feat Agent 실행

이 스킬은 얇은 런처다. 역할의 정본은 하네스 문서에 있다.

1. 정본 하네스를 읽는다: [sub-agent/backend-feat-agent.md](../../../sub-agent/backend-feat-agent.md)
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 입력을 확인한다.
   - `.harness/work/<작업명>/01-test-report.md`가 없으면 진행을 거부한다.
     테스트 단계가 먼저다. /test-agent를 안내한다.

3. 하네스의 책임 / 금지 / 완료 기준대로 실행한다.
   테스트는 절대 수정하지 않는다. 테스트가 잘못됐으면 반려 사유와 함께 멈춘다.

4. 완료 기준을 모두 충족하면 02-implementation-report.md를 작성하고,
   다음 단계로 /review-agent를 안내한다.
