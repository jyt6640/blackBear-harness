# Test Double By Responsibility

## 상태

accepted

---

## 문제 상황

Service / Validator 테스트에서 Mock과 Fake 중 무엇을 우선할지 결정이 필요했다.

이전에는 Fake Repository를 우선하는 방향으로 판단했지만,
Service와 Validator는 테스트 대상 책임이 다르다.

---

## 선택한 방향

테스트 더블은 Mock과 Fake 중 하나를 일괄 우선하지 않는다.
테스트 대상의 책임에 따라 선택한다.

- Service: Mock
- Validator: Fake 우선 고려
- Controller: Mock

---

## 선택 이유

### Service는 흐름을 검증한다

Application Service는 비즈니스 규칙 자체보다 유스케이스 흐름을 조율한다.

Service 테스트는 아래를 검증한다.

- 협력 객체를 호출하는가
- 예외 발생 시 흐름이 끊기는가
- 후속 작업이 호출되는가
- 보상 흐름이 실행되는가

이 목적에는 Mockito 기반 Mock이 자연스럽다.

---

### Validator는 상태 기반 검증 책임을 가진다

Validator는 Repository 조회 기반 검증을 담당한다.

따라서 Validator 테스트는 실제 상태에 따라 검증이 올바르게 동작하는지 확인해야 한다.
Fake Repository가 이 책임을 드러내기 좋다.

단, Fake 구현 비용이 과도하거나 단순 분기 검증이면 Mock도 사용할 수 있다.

---

## 트레이드오프

### Mock 과사용 위험

Mock 기반 Service 테스트는 내부 호출 구조에 과하게 의존할 수 있다.

따라서 Service 테스트는 구현 세부가 아니라 유스케이스 흐름과 협력 경계를 검증해야 한다.

---

### Fake 구현 비용

Validator 테스트에서 Fake를 쓰면 Fake 유지 비용이 생긴다.

Fake가 실제 Repository처럼 복잡해지면 테스트 목적을 다시 검토한다.

---

## 현재 판단

Mock은 외부 라이브러리 대체 수단만이 아니라 비즈니스 흐름을 검증하는 도구다.

Fake는 상태 기반 책임을 검증하는 도구다.

따라서 도구 선호가 아니라 테스트 대상 책임으로 선택한다.

---

## 재검토 신호

- Service 테스트가 호출 순서에 과하게 묶인다.
- Validator Fake가 실제 Repository처럼 복잡해진다.
- 테스트가 행위보다 구현 세부사항을 검증한다.
- Mock/Fake 선택이 책임 기준이 아니라 습관으로 굳어진다.
