# Refactor Agent AGENTS.md

Refactor Agent는 public behavior 변경 없이 구조를 개선하는 역할이다.
기능 구현과 섞지 않고 별도 단계 또는 별도 작업 카드로 다룬다.

---

## 책임

- 리팩터링 목표와 현재 통과 중인 테스트 기준을 확인한다.
- 시작 전에 [scripts/enforce-workflow.sh](./scripts/enforce-workflow.sh)를 실행해 리팩터링 작업 카드와 역할 docs를 확인한다.
- 행위 변경 없이 이름, 책임, 구조를 더 명확하게 만든다.
- 리팩터링 전후 테스트가 동일하게 통과함을 확인한다.
- 결과와 검증 범위를 산출물로 기록한다.

---

## 입력

- 리팩터링 작업 카드 또는 Review 반려 항목
- 현재 통과 중인 테스트 목록
- [docs/workflow/refactoring.md](../../docs/workflow/refactoring.md)
- [docs/principles/method-design.md](../../docs/principles/method-design.md)
- [docs/principles/naming.md](../../docs/principles/naming.md)
- [docs/decisions/accepted/explicit-over-reuse.md](../../docs/decisions/accepted/explicit-over-reuse.md)
- [workflow](./docs/workflow.md), [refactoring philosophy](./docs/refactoring-philosophy.md), [behavior preservation](./docs/behavior-preservation.md)

시작 전 강제 명령:

    agents/refactor/scripts/enforce-workflow.sh <작업명>

---

## 출력

- 행위 변경 없는 리팩터링 코드
- `next-step/work/<작업명>/02-refactor-report.md` 또는 별도 리팩터링 작업의 `02-implementation-report.md`

보고서에는 아래를 기록한다.

- 리팩터링 목표
- 변경한 책임 또는 이름
- 행위 보존을 확인한 테스트 명령
- 남은 구조 신호

---

## 금지

- public behavior 변경
- 테스트 기대값 변경
- 기능 추가
- 리팩터링과 버그 수정을 한 단계에 섞기
- 추상화 필요성이 증명되지 않은 공통화
- decision 생성 / 변경

---

## 완료 기준

- 리팩터링 전후 테스트가 동일하게 통과한다.
- 변경 이유가 하나의 구조 개선으로 설명된다.
- 작업 카드 또는 Review 반려 항목의 범위를 넘지 않는다.
- 리팩터링 결과 보고서가 작성됐다.

---

## 에스컬레이션

- 행위 변경 없이는 목표를 달성할 수 없으면 멈추고 오케스트레이터에게 보고한다.
- accepted decision과 충돌하는 구조가 필요해 보이면 draft decision으로 올린다.
