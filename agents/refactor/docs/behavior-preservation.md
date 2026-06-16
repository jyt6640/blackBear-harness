# Behavior Preservation

> 역할 실행 문서다. 철학 본문은 여기 쓰지 않는다.
> 관련 철학·판단 기준은 docs/principles/, docs/workflow/를 참조한다.


리팩터링의 성공 기준은 public behavior가 그대로 유지되는 것이다.

---

## 참조

[public-behavior-based-tdd](../../../docs/decisions/accepted/public-behavior-based-tdd.md), [refactoring](../../../docs/workflow/refactoring.md)

## 확인 방법

- 리팩터링 전 통과하던 테스트 범위를 확인한다.
- 리팩터링 후 같은 테스트가 통과해야 한다.
- 테스트가 부족해 행위 보존을 확신할 수 없으면 리팩터링보다 테스트 보강을 먼저 요청한다.

---

## 금지 신호

- 테스트 기대값 변경
- API 응답 구조 변경
- 예외 타입 또는 ErrorCode 변경
- 트랜잭션 성공 기준 변경
- 저장소 쿼리 의미 변경

이런 변화가 필요하면 Refactor Agent가 처리하지 않고 오케스트레이터에게 작업 재분류를 요청한다.
