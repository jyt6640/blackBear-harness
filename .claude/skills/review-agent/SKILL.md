---
name: review-agent
description: Test → Feat → Refactor → Review 릴레이의 마지막 단계. 작업 diff를 작업 카드와 하네스 기준으로 검증해 승인/반려를 03-review-report.md로 산출한다. Refactor 단계가 끝났을 때, 사용자가 "/review-agent" 또는 "리뷰하자"라고 할 때 사용한다.
---

# Review Agent 실행

이 스킬은 얇은 런처다. 역할의 정본은 하네스 문서에 있다.

1. 정본 하네스를 읽는다: [agents/review/AGENTS.md](../../../agents/review/AGENTS.md)
   그리고 역할별 docs를 확인한다: [agents/review/docs](../../../agents/review/docs/review-philosophy.md)
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 입력을 확인한다.
   - 00-task-card의 "시작 기준 commit"을 시작ref로 `agents/review/scripts/enforce-workflow.sh <작업명> <시작ref>`를 실행한다.
     커밋 체인 위반(형식, type 제한, 순서)은 반려 사유다.
   - `agents/review/scripts/enforce-workflow.sh <작업명>`를 먼저 실행한다.
   - `next-step/work/<작업명>/02-implementation-report.md`가 없으면 진행을 거부한다.
     refactor 카드는 `02-refactor-report.md`로 대체된다.
     구현 단계가 먼저다. /feat-agent를 안내한다.

3. 하네스의 책임 / 금지 / 완료 기준대로 실행한다.
   코드를 직접 수정하지 않는다. 판정과 수정 요구까지만이다.

4. 03-review-report.md를 작성하고 멈춘다. 직접 수정하거나 다음 단계를 실행하지 않는다.
   - 승인이면 사용자에게 "/orchestrate로 마무리(04-summary 작성과 정리)를 진행해주세요."라고 요청한다.
   - 반려면 수정 요구 목록과 함께 사유에 따라 재실행을 요청한다.
     구현 문제 → /feat-agent, 구조 문제 → /refactor-agent, 행위 정의 문제 → /test-agent.
