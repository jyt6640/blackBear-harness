# Infrastructure Exception Translation

## 상태

accepted

---

## 문제 상황

Validator가 중복을 먼저 검증하더라도,
동시성 때문에 DB 제약 조건 위반은 여전히 발생할 수 있다.

이때 `DataIntegrityViolationException` 같은 기술 예외를
누가 어디서 해석할지 결정이 필요했다.

선택지는 아래 두 가지였다.

### Application이 기술 예외를 직접 catch

    try {
        repository.save(order)
    } catch (DataIntegrityViolationException e) {
        ...
    }

### Infrastructure가 저장소 의미 예외로 변환

Infrastructure가 기술 예외를 저장소 의미의 예외로 바꾸고,
Application은 저장소 의미 예외만 다룬다.

---

## 선택한 방향

2단계 변환을 기본 입장으로 둔다.

1. Infrastructure는 구체 기술 예외를 잡아 저장소 의미의 커스텀 예외로 변환한다.

       PersistenceConflictException
       RepositoryConstraintViolationException

2. Application은 필요한 경우에만 저장소 의미 예외를 잡아
   유스케이스 맥락의 BusinessException / ErrorCode로 변환한다.

저장소 의미의 커스텀 예외는 unchecked로 둔다.

---

## 예외 위치

- 여러 도메인이 공유하는 저장소 예외는 global에 둔다.
  저장소 의미 예외는 모든 도메인을 관장하는 횡단 개념이므로
  global의 횡단 관심사 기준과 충돌하지 않는다.
- 특정 도메인에만 의미 있는 예외는 해당 도메인에 둔다.
- 특정 Infrastructure 구현체에만 의미 있는 예외는 infrastructure 내부에 둔다.

---

## 선택 이유

### Infrastructure 출처 명확화

저장소 의미 예외는 문제가 Infrastructure에서 왔음을 분명하게 만든다.

### Application의 기술 결합 차단

구체 기술 예외를 Application이 세부 해석하면,
저장 기술 변경이 유스케이스 코드까지 전파된다.

### unchecked가 자연스러움

Repository 제약 위반은 모든 호출자가 복구할 수 있는 예외가 아니다.
필요한 유스케이스만 잡아 의미를 부여하는 흐름이 자연스럽다.

---

## 트레이드오프

### 변환 코드 증가

예외 클래스와 변환 보일러플레이트가 늘어난다.

### 기술 고정성이 높으면 과할 수 있음

기술 스택이 사실상 고정인 프로젝트에서는
잘 추상화된 Spring 예외를 그대로 쓰는 것이 더 단순할 수 있다.

전파 허용 여부는 프로젝트별로 정한다.
→ [spring-dao-exception-propagation](../pending/spring-dao-exception-propagation.md)

---

## 현재 판단

이 하네스는 기술 예외 차단을 절대 규칙이 아니라 기본 입장으로 둔다.

구체 기술 예외를 Application이 세부 해석하는 구조는 피하되,
프로젝트 규모, 저장소 교체 가능성, 기술 고정성에 따라
pending 결정으로 완화할 수 있다.

---

## 재검토 신호

- 의미 부여 없이 변환만 하는 보일러플레이트가 반복된다.
- 저장소 의미 예외 종류가 과도하게 증가한다.
- Application이 저장소 예외를 잡지 않고 항상 그대로 통과시킨다.
- 저장소 예외와 유스케이스 예외의 경계가 흐려진다.
