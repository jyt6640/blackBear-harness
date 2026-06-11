---
name: harness-interview
description: 프로젝트 하네스 인터뷰를 실행해 base 하네스의 비워진 슬롯을 채우고 decision과 산출물을 만든다. 새 프로젝트 첫 작업인데 프로젝트 decisions가 없을 때, 기존 코드에 하네스가 없을 때, 상위 전제 질문이 반복될 때, 사용자가 "하네스 만들자" "인터뷰 하자"라고 할 때 사용한다.
---

# Harness Interview 실행

이 스킬은 얇은 런처다. 절차의 정본은 문서에 있고, 여기에 절차를 복사하지 않는다.

1. 정본 문서를 읽는다.
   - [docs/workflow/harness-interview.md](../../../docs/workflow/harness-interview.md) — 질문 절차, 규모 프로파일, 결정 영역
   - [docs/workflow/how-to-create-project-harness.md](../../../docs/workflow/how-to-create-project-harness.md) — 산출물 구조, 생성 경로
   - base 하네스 저장소 밖에서 실행 중이면, 프로젝트 AGENTS.md의 base 선언 경로에서 위 문서를 찾는다.

2. 종료 조건을 먼저 확인한다.
   프로젝트 하네스가 이미 있으면 전체 인터뷰를 다시 하지 않고,
   문서의 종료 조건에 따라 부분 재진입만 한다.

3. 문서 절차대로 실행한다.
   - 1단계 규모 파악으로 프로파일을 정하고, 프로파일에 맞는 영역 질문만 한다.
   - 기존 코드가 있으면 먼저 관찰하고 확인 질문으로 전환한다.
   - 답마다 TEMPLATE 형식의 draft decision을 작성한다. 작성 계기와 근거를 남긴다.
   - 모르면 pending, 해당 없으면 not-applicable로 기록한다.

4. 산출물을 만든다: 프로젝트 AGENTS.md(base diff), decisions/, development-guideline.md.

5. 사람이 확인한 draft만 accepted로 승격한다.
