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
   - 기능을 public behavior 단위로 분할한다.
   - 카드가 2장 이상이면 [next-step/templates/backlog.md](../../../next-step/templates/backlog.md)를 복사해
     `next-step/work/backlog.md`에 카드 목록과 순서를 먼저 기록한다.
     카드 목록을 대화 기억에만 두지 않는다.
   - 백로그 순번의 첫 카드만 [next-step/templates/00-task-card.md](../../../next-step/templates/00-task-card.md)를 복사해 컴파일한다.
     카드의 "시작 기준 commit"에 `git rev-parse HEAD` 값을 기록한다.
     뒤 카드를 미리 만들지 않는다. (앞 카드의 decision이 뒤 카드 지침에 반영되어야 한다)
   - 카드 작성 후 멈춘다. Test 단계를 직접 시작하지 않는다.
     사용자에게 "작업 카드 작성 완료. /test-agent를 실행해주세요."라고 요청한다.
   - 세션이 새로 시작됐고 `next-step/work/backlog.md`가 있으면, 백로그를 읽고 진행 상태에서 이어간다.

3. 마무리 요청이면 (`03-review-report.md`가 승인 상태):
   - `04-summary.md`를 작성한다.
   - 백로그가 있으면 해당 카드 상태를 갱신하고, 뒤 카드에 영향을 주는 decision을 누적 메모에 적는다.
   - 영구화할 내용만 docs / decisions / 커밋 메시지 / PR 설명으로 승격한다.
   - `next-step/history/<작업명>`이 이미 있으면 덮어쓰지 않고 중단해 사용자에게 확인한다.
   - `next-step/work/<작업명>`을 `next-step/history/<작업명>/`으로 이동하고 완료 기록으로 커밋한다.
   - 백로그에 다음 카드가 있으면 멈추고 사용자에게 다음 카드의 /orchestrate 실행을 요청한다.
     모든 카드가 끝났으면 `next-step/work/backlog.md`를
     `next-step/history/<백로그명>-backlog.md`로 이동하고 함께 커밋한다.

4. 오케스트레이터는 직접 구현 / 테스트 / 리뷰를 하지 않는다.
