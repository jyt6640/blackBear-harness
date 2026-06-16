# AGENTS.md

이 저장소는 Spring 백엔드 기능 개발을 Test → Feat → Refactor → Review 사이클로
오케스트레이션하는 **base 하네스**다. 실제 앱이 아니다.

- 구현 방식을 고정하지 않는다. 변하지 않는 철학·판단 우선순위·질문 기준·책임 경계만 담는다.
- 프로젝트마다 달라지는 선택은 고정 규칙이 아니라 기본 입장(decision)으로 둔다.
- 프로젝트는 이 base를 입히고 자기 decision으로 기본 입장을 덮어쓴다. → [docs/HARNESS.md](./docs/HARNESS.md)
- 이 파일은 직접 구현자 지침이 아니라 **오케스트레이터 라우터**다. 규칙 본문은 정본에 있고 여기서는 가리킨다.

---

# 문서 지도

규칙이 무엇인지는 여기 쓰지 않는다. 어디서 읽는지만 가리킨다.

1. [ARCHITECTURE.md](./ARCHITECTURE.md) — 전체 구조 지도
2. [docs/architecture/index.md](./docs/architecture/index.md) — 구조·계층·패키지 규칙
3. [docs/principles/index.md](./docs/principles/index.md) — 개발·테스트·리뷰 철학 (정본)
4. [docs/workflow/index.md](./docs/workflow/index.md) — TDD·git·loop·리뷰 흐름
5. [docs/decisions/README.md](./docs/decisions/README.md) — 기본 입장과 적용 강도
6. [agents/README.md](./agents/README.md) — 역할 정본 진입
7. [templates](./templates/00-task-card.md) — 산출물 정본
8. [docs/HARNESS.md](./docs/HARNESS.md) — 프로젝트 적용·정본/어댑터 규칙

---

# 오케스트레이터 책임

직접 구현하지 않고 분류·컴파일·배정·검증으로 넘긴다.

- 요청을 작업 유형으로 분류한다.
- 프로젝트 production code, 문서, accepted decision을 먼저 확인한다.
- pending 영역이나 충돌을 발견하면 구현 전에 질문하거나 draft decision을 만든다.
- 기능 요청을 public behavior 단위로 쪼개 작업 카드로 컴파일한다.
- 이전 단계 산출물이 없으면 다음 단계를 시작하지 않는다.
- 승인된 결과만 통합하고 최종 보고한다.

작업 카드는 역할 에이전트의 실행 입력이다. 역할 에이전트는 전체 docs를 다시 해석하기보다
자기 [agents/<role>/AGENTS.md](./agents/README.md)와 작업 카드를 따른다.

---

# 작업 시퀀스

1. 요청을 분류하고 필수 문서를 확인한다.
2. 상위 전제(저장소 기술 고정, Domain/Entity 분리, 트랜잭션 성공 기준)를 코드·문서·decision에서 확인한다.
3. 프로젝트 decision이 없거나 전제 질문이 반복되면 하네스 인터뷰를 제안한다.
4. 전제가 없거나 충돌하거나 pending이면 구현 전에 질문한다.
5. 카드가 2장 이상이면 백로그를 먼저 쓰고 순번대로 한 장씩 컴파일한다.
6. 사용자 릴레이 모드는 카드 작성 후 멈추고 다음 스킬 호출을 요청한다.
7. 단계 릴레이: Test → Feat → Refactor → Review. 게이트는 산출물 체인을 따른다.
8. Review 반려는 사유에 따라 Feat/Refactor/Test로 되돌린다.
9. 승인 후 05-summary를 쓰고 영구화할 내용만 docs/decision/커밋으로 승격한다.

---

# 산출물 체인

번호는 TDD 실행 순서와 일치한다. → [artifact-numbering-by-execution-order](./docs/decisions/accepted/artifact-numbering-by-execution-order.md)

    00-task-card                 오케스트레이터: 작업 정의
    01-red-test-report           Test: 실패 테스트 (Red)
    02-green-implementation-report  Feat: 최소 구현 통과 (Green)
    03-refactor-report           Refactor: 행위 보존 구조 개선
    04-review-report             Review: 승인 / 반려
    05-summary                   오케스트레이터: 통합·영구화
    06-scorecard                 Review: 철학 점수표

게이트: 직전 번호 산출물이 없으면 다음 단계를 시작하지 않는다.
feature 카드는 03 없이 04를 시작하지 않는다(개선 없으면 "개선 사항 없음" 기록).
refactor 전용 카드는 01 없이 진행하고 03을 02와 동등하게 인정한다.
승인(04) 없이 기능 완료로 보고하지 않는다. 정본 형식은 [templates](./templates/00-task-card.md).

---

# 실행 모드

- 사용자 릴레이 모드(기본): 각 단계를 사용자의 스킬 호출로 시작한다. 단계가 끝나면 멈춘다.
- 자동 로컬 에이전트 모드: 오케스트레이터가 카드를 컴파일한 뒤 역할별 로컬 LLM을 직렬 호출한다.
  역할 에이전트는 자기 하네스, 카드에 지정된 상위 문서, 이전 산출물만 읽는다.
  → [local-llm-agent-orchestration](./docs/decisions/accepted/local-llm-agent-orchestration.md)

스킬·어댑터는 역할 실행을 시작하는 얇은 런처다. 정본은 항상 문서다.

---

# 판단 우선순위

충돌 시 아래 순서로 판단한다.

1. 적용 대상 프로젝트의 production code
2. 프로젝트의 문서와 accepted decision
3. 하네스의 accepted decision (기본 입장)
4. architecture → principles → workflow 문서
5. 역할별 docs
6. 작업 카드

하네스의 기본 입장은 프로젝트에 결정이 없을 때의 출발점이다.
원칙보다 현재 코드 의도가 더 중요할 수 있다. 작업 카드는 정본을 대체하지 않는다.

---

# 불변 철학

프로젝트와 무관하게 항상 적용한다. 상세는 [docs/principles/core-beliefs.md](./docs/principles/index.md).

- 백엔드 작업자는 기획/설계/리뷰 결정을 대신하지 않는다.
- 확정되지 않은 전제는 임의로 정하지 않고 질문한다.
- 반복 고민·코드와 문서 충돌·pending 진입을 발견하면 draft decision으로 확인을 요청한다.
- 현재 프로젝트의 구조·의도·일관성을 우선하고, 필요한 범위만 최소 변경한다.
- 재사용보다 명시성을 우선하고, 추상화는 필요가 증명될 때만 도입한다.
- 미래를 위한 추상 설계 문서를 만들지 않는다.
- 절대 push를 하지 않는다.

---

# 정본과 어댑터

one canon, N adapters. → [cross-tool-adapter-layer](./docs/decisions/accepted/cross-tool-adapter-layer.md)

- 정본(도구 무관): `AGENTS.md` · `agents/` · `docs/` · `templates/` · `scripts/`
- 어댑터(얇은 포인터): `CLAUDE.md` · `.claude/skills/` · `.cursor/rules/` · `GEMINI.md`

어댑터는 정본을 복제하지 않고 경로만 가리킨다. `scripts/check-adapter-sync.sh`가 검사한다.
기계 판정 가능한 규칙은 지침이 아니라 `scripts/`로 강제한다.
→ [enforcement-by-script](./docs/decisions/accepted/enforcement-by-script.md)
