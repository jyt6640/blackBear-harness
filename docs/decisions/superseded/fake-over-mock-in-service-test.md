# Fake Over Mock In Service Test

## 상태

superseded

대체 결정:

- [test-double-by-responsibility.md](../accepted/test-double-by-responsibility.md)

---

## 문제 상황

Service / Validator 테스트에서
Repository를 어떤 방식으로 대체할지 결정이 필요했다.

선택지는 아래 두 가지였다.

### Mock 기반 테스트

    given(repository.findById(...))
        .willReturn(...)

### Fake 기반 테스트

    FakeOrderRepository

를 직접 구현해 사용하는 방식이다.

---

## 이전 선택 방향

Service / Validator 테스트에서는 Fake Repository를 우선 사용한다고 판단했다.

Mock은 호출 여부 검증이나 외부 협력 검증이 필요한 경우에만 사용하는 것으로 보았다.

---

## 선택 이유

### 흐름 중심 테스트 가능

Fake는 실제 상태를 가지므로,
유스케이스 흐름을 자연스럽게 검증할 수 있다.

즉:
- 저장
- 조회
- 상태 변경

흐름을 실제처럼 테스트 가능하다.

---

### 구현 세부사항 의존 감소

Mock 기반 테스트는:

    verify(...)
    given(...)

호출 구조에 과하게 의존하기 쉽다.

그 결과:
- 리팩토링 취약
- 내부 구현 변경에 민감
- 테스트 의도 약화

문제가 발생할 수 있다.

---

### 테스트 가독성 증가

Fake 기반 테스트는
행위 중심으로 읽히는 경우가 많다.

좋은 예시:

    repository.save(order)

    service.cancel(...)

    assertThat(order.status()).isEqualTo(...)

---

## Mock 사용 기준

아래 상황에서는 Mock 사용이 자연스럽다.

- 외부 API 호출 검증
- 이벤트 발행 여부 검증
- Controller → Service 위임 검증
- 협력 객체 호출 여부 자체가 중요한 경우

---

## 트레이드오프

### Fake 구현 비용 증가

Fake 클래스를 직접 작성해야 한다.

---

### Repository 구조 변경 영향 가능

Repository 인터페이스 변경 시
Fake 구현도 함께 수정해야 한다.

---

## 현재 판단

이 결정은 더 이상 현재 기준이 아니다.

Service와 Validator는 테스트 대상 책임이 다르다.

- Service는 유스케이스 흐름과 협력 호출을 검증한다.
- Validator는 저장소 조회 기반 검증 책임을 검증한다.

따라서 Service / Validator 테스트에 Fake를 일괄 우선하지 않는다.
테스트 더블은 테스트 대상의 책임에 따라 Mock 또는 Fake를 선택한다.

---

## 대체 이유

- Service 테스트에서 Fake를 강제하면 orchestration 검증보다 상태 재현에 집중할 수 있다.
- Mock은 외부 라이브러리 대체 수단만이 아니라 비즈니스 흐름을 검증하는 도구가 될 수 있다.
- Validator는 상태 기반 검증 책임을 가지므로 Fake가 자연스러운 경우가 많다.
- 도구 선호보다 테스트 대상 책임이 더 중요한 기준이다.
