---
name: draft-decision
description: 현재 대화나 코드 관찰에서 발견한 결정 사항을 draft decision으로 작성한다. 같은 고민이 반복될 때, 코드와 문서가 충돌할 때, pending 영역에 진입했을 때, 기본 입장을 뒤집는 지시를 받았을 때, 새 기술이나 패턴이 추가될 때 사용한다.
---

# Draft Decision 작성

이 스킬은 얇은 런처다. 형식의 정본은 문서에 있고, 여기에 형식을 복사하지 않는다.

1. 정본 문서를 읽는다.
   - [docs/decisions/TEMPLATE.md](../../../docs/decisions/TEMPLATE.md) — 문서 형식
   - [docs/decisions/README.md](../../../docs/decisions/README.md) — 상태 기준과 기록 기준
   - base 하네스 저장소 밖에서 실행 중이면, 프로젝트 AGENTS.md의 base 선언 경로에서 위 문서를 찾는다.

2. 중복을 확인한다.
   같은 주제의 decision이 이미 있으면 새로 만들지 않고
   그 문서의 갱신 또는 supersede를 제안한다.

3. TEMPLATE 형식으로 decisions/draft/에 작성한다.
   작성 계기(인터뷰 답변 / 코드 관찰 / 반복 질문 / 기본 입장 충돌)와
   근거(파일 경로, 코드 위치)를 반드시 남긴다.

4. 사람에게 확인을 요청한다. draft는 확인 전까지 어떤 효력도 없다.

5. 확인 결과에 따라 accepted / rejected / pending / not-applicable로 이동하거나 삭제한다.
