# Workflow

이 문서는 프로젝트의 작업 흐름 기준을 정리한다.

워크플로우는 단순 작업 순서가 아니라,
생각 → 구현 → 검증 → 개선 과정을 반복하는 흐름으로 본다.

---

## 핵심 방향

- 기능은 작은 단위로 나누어 구현한다.
- 안쪽 도메인부터 바깥 방향으로 구현한다.
- 테스트 가능한 상태를 유지하며 작업한다.
- 리팩토링은 작업 과정 전체에서 반복한다.
- 코드 리뷰를 통해 설계 판단을 개선한다.

---

## 기본 작업 흐름

기능은 아래 순서로 구현한다.

1. 오케스트레이터가 작업 카드를 작성한다.
2. Test Agent가 실패 테스트와 01-test-report를 작성한다.
3. Feat Agent가 실패 테스트를 통과시키는 최소 구현을 작성한다.
4. Review Agent가 산출물과 diff를 검증한다.
5. 필요한 리팩터링은 별도 Refactor 단계 또는 별도 작업 카드로 분리한다.

기능 단위로 끝까지 구현한 뒤 다음 기능으로 이동한다.
역할은 한 작업자가 겸임할 수 있지만 산출물 경계는 생략하지 않는다.

---

## TDD

테스트를 먼저 작성하는 흐름을 지향한다.

- 기대 행위를 먼저 표현한다.
- 작은 단위로 실패 → 구현 → 개선 사이클을 반복한다.
- 테스트를 설계 피드백 도구로 사용한다.

→ [tdd.md](./tdd.md)

---

## Refactoring

리팩토링은 지속적으로 수행한다.

- 어색함은 구조 신호로 본다.
- 이름부터 먼저 개선한다.
- 구조보다 의도를 더 명확하게 만든다.

→ [refactoring.md](./refactoring.md)

---

## Code Review

코드 리뷰는 설계와 의도를 함께 검토하는 과정이다.

- 코드보다 의도를 먼저 이해한다.
- 현재 프로젝트 기준을 우선한다.
- 근거 없는 일반론 적용을 지양한다.

→ [code-review.md](./code-review.md)

---

## Git Convention

Git 기록은 작업 흐름과 변경 의도를 표현해야 한다.

- 작은 단위로 자주 커밋한다.
- 기능 단위로 PR을 구성한다.
- 변경 이유를 추적 가능해야 한다.

→ [git-convention.md](./git-convention.md)

---

## 추가 문서

- [how-to-add-new-feature.md](./how-to-add-new-feature.md)
- [local-agent-orchestration.md](./local-agent-orchestration.md)
- [how-to-review-legacy-code.md](./how-to-review-legacy-code.md)
- [how-to-create-project-harness.md](./how-to-create-project-harness.md) — 하네스 생성 시에만
- [harness-interview.md](./harness-interview.md) — 하네스 생성 시에만
- [loop-engineering.md](./loop-engineering.md) — 철학 점수화와 프롬프트 개선 루프

---

## 판단 기준

작업 흐름이 헷갈리면 아래 질문으로 판단한다.

- 지금 가장 작은 단위로 작업하고 있는가?
- 테스트 가능한 상태를 유지하고 있는가?
- 구조보다 의도를 더 명확하게 만들고 있는가?
- 다음 수정자가 흐름을 이해 가능한가?

YES라면 좋은 작업 흐름 가능성이 높다.
