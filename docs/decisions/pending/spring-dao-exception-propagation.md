# Spring DAO Exception Propagation

## 상태

pending

---

## 문제 상황

저장소 구현체의 기술 예외를 Application Layer까지
전파하도록 허용할지 결정이 필요하다.

Spring 기반이라면 `DataAccessException`처럼
잘 추상화된 예외의 전파 허용 여부를 함께 결정한다.

프로젝트의 기술 고정성에 따라 답이 달라지므로
하네스의 기본 입장으로 고정하지 않는다.

인터페이스 계약에 예외를 드러내는 수준은
[repository-exception-contract](./repository-exception-contract.md)에서 함께 결정한다.

---

## 판단 기준

- 기술 스택(Spring, DB)이 사실상 고정인가
- 저장소 교체 가능성이 있는가 (MSA, 멀티 스토리지, 외부 저장소 전환 계획)
- 프로젝트 규모와 운영 방식

---

## 잠정 기준

프로젝트에 결정이 없으면
[infrastructure-exception-translation](../accepted/infrastructure-exception-translation.md)의
2단계 변환을 따른다.

DB 종속이 확정된 프로젝트라면
잘 추상화된 예외(Spring이라면 `DataAccessException` 계열)의
Application 전파를 허용할 수 있다.

DB 제약이 비즈니스 흐름의 일부가 되는 프로젝트에서는
저장소 예외가 흐름에 드러나는 것이 자연스럽기 때문이다.

허용하는 경우에도 구체 기술 예외(JDBC 드라이버, JPA 구현체 예외)를
Application이 세부 해석하는 구조는 피한다.

---

## 프로젝트 결정 시점

프로젝트 하네스 생성 시
저장소 기술 고정 여부(상위 전제)와 함께 결정한다.

---

## 재검토 신호

- 고정이라 판단했던 기술 스택에 교체 요구가 생긴다.
- Application 코드가 Spring DAO 예외의 세부 타입으로 분기하기 시작한다.
- 인터페이스 계약과 실제 던지는 예외가 어긋난다.
