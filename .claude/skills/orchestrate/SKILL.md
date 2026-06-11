---
name: orchestrate
description: 오케스트레이터로 기능 개발 릴레이를 시작하거나 마무리한다. 기능 요청을 public behavior 단위로 분할해 next-step/work/<작업명>/00-task-card.md를 컴파일하고 멈춘다. 사용자가 "기능 개발 시작하자", "/orchestrate", "작업 카드 만들자"라고 할 때, 또는 Review 승인 후 04-summary 마무리가 필요할 때 사용한다.
---

# Orchestrator 실행

이 스킬은 얇은 런처다. 정본은 루트 AGENTS.md에 있다.

1. 정본을 읽는다: [AGENTS.md](../../../AGENTS.md)의 오케스트레이터 책임, 작업 시퀀스, 산출물 체인.
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 시작 요청이면:
   - 요청을 분류하고 상위 전제를 확인한다. 전제가 없으면 구현 전에 질문한다.
   - 기능을 public behavior 단위로 분할하고,
     [next-step/templates/00-task-card.md](../../../next-step/templates/00-task-card.md)를 복사해 작업 카드를 컴파일한다.
   - 카드 작성 후 멈춘다. Test 단계를 직접 시작하지 않는다.
     사용자에게 "작업 카드 작성 완료. /test-agent를 실행해주세요."라고 요청한다.

3. 마무리 요청이면 (`03-review-report.md`가 승인 상태):
   - `04-summary.md`를 작성한다.
   - 영구화할 내용만 docs / decisions / 커밋 메시지 / PR 설명으로 승격한다.
   - `next-step/work/<작업명>` 삭제를 안내한다.

4. 오케스트레이터는 직접 구현 / 테스트 / 리뷰를 하지 않는다.
