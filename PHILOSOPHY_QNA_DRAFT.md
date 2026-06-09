# step2 코드 기반 철학 QnA 초안

이 문서는 `spring-roomescape-waiting`의 `step2` 코드를 관찰하면서,
현재 하네스 철학과 충돌하거나 새 원칙이 필요한 지점을 모으는 작업 초안이다.

이 문서는 `AGENTS.md`나 `docs/**`에 바로 반영하지 않는다.
이후 하네스 문서를 다시 정리할 때 판단 재료로 사용한다.

---

## 기준 코드

- Repository: <https://github.com/jyt6640/spring-roomescape-waiting/tree/step2>
- 관찰 기준: Spring 백엔드, 도메인 중심 패키지, JDBC Repository, Application Service, Validator, Reference Adapter, 테스트 구조

---

## 기존 하네스와의 관계

현재 하네스는 아래 기준을 이미 가진다.

- Domain 중심 패키지 구조
- Service는 유스케이스 흐름만 조율
- 저장소 조회 기반 검증은 Application Validator 책임
- Repository 인터페이스는 Domain에 두고 구현체는 Infrastructure에 둠
- HTTP 입력 null / blank 검증은 Request DTO 책임
- Service / Validator 테스트는 Fake Repository 우선
- Production class는 직접 테스트하는 것을 기본으로 함
- 의미 없는 Util / Helper / Manager / Common 구조는 지양

이 문서는 위 기준을 대체하지 않는다.
다만 step2 코드에서 반복적으로 드러나는 실전 판단을 바탕으로,
기존 문서에 없는 공백이나 충돌 지점을 질문으로 남긴다.

---

## QnA 기록 규칙

각 항목은 아래 형식을 따른다.

1. 코드 관찰
2. 기존 하네스 기준
3. 충돌 또는 공백
4. 질문
5. 사용자 답변
6. 임시 판단
7. 하네스 반영 후보 위치

기록 원칙:

- 외부 코드 관찰과 사용자 답변을 섞지 않는다.
- 답변 전에는 `미답변` 상태로 둔다.
- 답변 후에도 곧바로 accepted로 확정하지 않고 임시 판단을 남긴다.
- 하네스 반영은 별도 작업에서 진행한다.
- 코드 한 사례만으로 일반 규칙을 만들지 않는다.

---

## 관찰된 철학 후보

### 채택 후보

- 도메인 간 직접 의존이 부담될 때 `Reference` 인터페이스와 Adapter 구현으로 협력 경계를 만든다.
- 예약 삭제, 사용자 취소, 대기 승격처럼 race condition이 가능한 유스케이스는 별도 동시성 테스트를 둔다.
- DB 제약 조건은 마지막 방어선으로 두고, Application Validator와 예외 변환이 중복 방어를 담당한다.
- 스케줄러는 직접 비즈니스 판단을 하지 않고 Service 유스케이스 호출만 담당한다.

### 보류 후보

- 예약 삭제 후 대기 승격 실패를 원 작업 실패로 볼지, 부가 작업 실패로 보고 로그만 남길지 결정이 필요하다.
- `FOR UPDATE`, unique constraint, `DataIntegrityViolationException` 처리 기준을 어느 문서까지 명시할지 결정이 필요하다.
- 다른 도메인 Repository를 Service가 직접 참조하는 경우와 Reference Adapter를 거치는 경우의 기준이 필요하다.

### 거절 후보

- `ValidationUtils` 같은 전역 검증 유틸은 현재 하네스의 `common/util` 거절 철학과 충돌 가능성이 있다.
- Request DTO에서 처리할 null / blank 검증을 Domain 또는 Service로 옮기는 방식은 현재 기준과 충돌한다.
- Service 테스트 하나로 Domain / Validator / Scheduler 책임 검증을 대체하는 방식은 현재 기준과 충돌한다.

---

## QnA

### Q1. 도메인 간 참조를 Reference 인터페이스와 Adapter로 분리할 것인가?

#### 코드 관찰

step2 코드에는 `WaitingReference`, `ReservationTimeReference`, `ThemeReference` 같은 인터페이스와
이를 구현하는 `WaitingReferenceAdapter`, `ReservationTimeReferenceAdapter`, `ThemeReferenceAdapter`가 존재한다.

예를 들어 Waiting 도메인은 Reservation 저장 여부를 직접 알지 않고,
`WaitingReference`를 통해 "대기 생성 가능 여부"와 "대기를 예약으로 승격"하는 협력을 요청한다.

#### 기존 하네스 기준

- Domain은 기술 구현을 알지 못한다.
- Service는 흐름만 조율한다.
- 도메인 간 책임 침범을 피한다.
- 저장소 조회 기반 검증은 Application Validator에 둔다.

#### 충돌 또는 공백

기존 하네스는 도메인 간 협력 원칙은 가지고 있지만,
다른 도메인의 Repository를 직접 참조하는 Service와
Reference 인터페이스를 통한 Adapter 협력 중 무엇을 기본으로 할지 명시하지 않는다.

#### 질문

다른 도메인의 상태나 행위가 필요할 때,
Service가 다른 도메인의 Repository를 직접 참조하는 것을 허용할까?
아니면 `Reference` 인터페이스와 Adapter를 통해 도메인 간 협력 경계를 명시할까?

#### 사용자 답변

미답변

#### 임시 판단

도메인 간 협력이 반복되거나 한 도메인이 다른 도메인의 내부 저장 구조를 알아야 하는 상황이면
`Reference` 인터페이스와 Adapter를 채택 후보로 둔다.

단순 조회 한두 개까지 무조건 Adapter로 감싸면 위임 객체만 늘어날 수 있으므로 기준이 필요하다.

#### 하네스 반영 후보 위치

- `docs/architecture/domain-boundary.md`
- `docs/architecture/layered-architecture.md`
- `docs/decisions/pending/domain-reference-adapter.md`

---

### Q2. 전역 검증 유틸을 하네스 철학상 거절 사례로 볼 것인가?

#### 코드 관찰

step2 코드에는 `global.validation.ValidationUtils`가 있고,
`requireNotBlank`, `requireNotNull` 같은 공통 입력 검증 메서드를 제공한다.

동시에 Request DTO에는 `@NotBlank`, `@NotNull`도 사용된다.

#### 기존 하네스 기준

- HTTP 입력의 null / blank 검증은 Request DTO에서 수행한다.
- 의미 없는 공통화와 추상화를 지양한다.
- `common/util/helper` 성격의 전역 유틸은 rejected decision으로 관리한다.

#### 충돌 또는 공백

`ValidationUtils`는 전역 유틸 구조이므로 기존 하네스의 `common-util-package` 거절 기준과 충돌할 수 있다.
다만 Spring validation annotation으로 표현하기 어려운 수동 검증이 필요한 경우의 기준은 아직 충분히 세분화되어 있지 않다.

#### 질문

`ValidationUtils` 같은 전역 검증 유틸은 원칙적으로 거절할까?
아니면 Request DTO에서 처리하기 어려운 입력 계약 검증에 한해 제한적으로 허용할까?

#### 사용자 답변

미답변

#### 임시 판단

현재 기준으로는 거절 후보에 둔다.
허용하려면 "횡단 관심사"가 아니라 "입력 계약 표현의 기술적 한계"라는 조건이 명확해야 한다.

#### 하네스 반영 후보 위치

- `docs/decisions/rejected/common-util-package.md`
- `docs/principles/testing.md`
- `docs/architecture/domain-boundary.md`

---

### Q3. 예약 삭제 후 대기 승격 실패를 원 작업 실패로 볼 것인가?

#### 코드 관찰

step2 코드의 `ReservationService.deleteReservation`, `cancelReservation`, `updateReservationSchedule`은
예약 삭제 또는 변경 후 `waitingService.promoteNextWaiting(...)`을 호출한다.

승격 중 `BusinessException` 또는 `DataAccessException`이 발생하면 예외를 다시 던지지 않고 로그만 남긴다.
즉 예약 삭제나 변경 자체는 성공으로 유지한다.

#### 기존 하네스 기준

- 하나의 유스케이스는 하나의 트랜잭션으로 처리한다.
- Service는 유스케이스 흐름과 트랜잭션 경계를 관리한다.
- 예외 위치는 검증 책임 위치와 일치해야 한다.

#### 충돌 또는 공백

기존 하네스는 부가 후속 작업 실패를 원 유스케이스 실패로 볼지,
보상 가능한 실패로 볼지에 대한 기준이 없다.

대기 승격은 비즈니스적으로 중요하지만,
예약 삭제 자체와 동일한 원자성 요구를 가지는지는 별도 판단이 필요하다.

#### 질문

예약 삭제 후 대기 승격 실패는 예약 삭제 실패로 rollback해야 할까?
아니면 삭제는 성공시키고 승격 실패는 로그 또는 재시도 대상으로 남길까?

#### 사용자 답변

미답변

#### 임시 판단

현재 step2 구현은 "원 작업 성공, 후속 승격 실패는 격리" 방향이다.
이 방향을 채택하려면 후속 작업 실패의 사용자 영향과 재처리 전략을 함께 문서화해야 한다.

#### 하네스 반영 후보 위치

- `docs/architecture/transactions.md`
- `docs/principles/exceptions.md`
- `docs/decisions/pending/compensating-follow-up-failure.md`

---

### Q4. 동시성 위험 유스케이스는 별도 concurrency test 원칙을 만들 것인가?

#### 코드 관찰

step2 코드에는 `ReservationConcurrencyTest`가 있고,
예약 삭제와 대기 신청, 관리자 삭제와 사용자 취소, 대기 승격과 대기 취소가 동시에 실행되는 상황을 검증한다.

테스트는 `CountDownLatch`, `ExecutorService`, 실제 Spring Context와 DB를 사용해 race condition을 재현한다.

#### 기존 하네스 기준

- Service / Validator 테스트는 Spring Context 없이 Fake Repository를 우선한다.
- Repository 테스트는 실제 DB 기반으로 SQL과 매핑 결과를 검증한다.
- `@SpringBootTest`는 전체 흐름 검증에만 사용한다.

#### 충돌 또는 공백

동시성 테스트는 Service 단위 테스트도 아니고 단순 Repository 테스트도 아니다.
Fake Repository로는 DB lock, unique constraint, transaction race를 검증하기 어렵다.

현재 하네스에는 동시성 테스트를 언제 허용하고 어떻게 분류할지 기준이 부족하다.

#### 질문

예약, 결제, 재고, 대기 순번처럼 동시성 위험이 있는 유스케이스는
별도의 concurrency test 범주를 만들고 실제 DB 기반 통합 테스트를 허용할까?

#### 사용자 답변

미답변

#### 임시 판단

동시성 위험이 도메인 요구사항의 일부라면 별도 테스트 범주가 필요하다.
단, 모든 Service 테스트를 `@SpringBootTest`로 넓히는 근거가 되어서는 안 된다.

#### 하네스 반영 후보 위치

- `docs/principles/testing.md`
- `docs/workflow/tdd.md`
- `docs/decisions/pending/concurrency-test-boundary.md`

---

### Q5. DB lock, unique constraint, DataIntegrityViolationException 처리 기준을 어디까지 문서화할 것인가?

#### 코드 관찰

step2 코드에는 `findByIdForUpdate`, `findByDateAndTimeIdAndThemeIdForUpdate` 같은 Repository 메서드가 있고,
저장 시 `DataIntegrityViolationException`을 잡아 도메인 의미의 `BusinessException`으로 변환하는 흐름이 있다.

중복 검증은 Application Validator에서 먼저 수행하지만,
DB 제약 조건 위반도 마지막 방어선으로 처리한다.

#### 기존 하네스 기준

- 저장소 조회 기반 검증은 Application Validator에 둔다.
- Repository는 저장 기술과 SQL, 매핑을 담당한다.
- Service는 흐름을 조율하고 트랜잭션 경계를 관리한다.
- 기술 예외보다 도메인 의미를 우선한다.

#### 충돌 또는 공백

Application Validator의 사전 검증과 DB 제약 조건의 사후 방어가 함께 존재할 때,
각 책임을 어디까지 문서화할지 아직 선명하지 않다.

특히 `DataIntegrityViolationException`을 Service에서 잡는 것이 적절한지,
Repository 구현체에서 도메인 예외로 변환해야 하는지 판단 기준이 필요하다.

#### 질문

중복과 race condition에 대해
Application Validator는 사전 의미 검증을 맡고,
DB unique constraint는 최종 일관성 방어를 맡는다는 원칙을 명시할까?

기술 예외 변환은 Service에서 유스케이스 의미로 변환할까,
Repository에서 저장 의미로 변환할까?

#### 사용자 답변

미답변

#### 임시 판단

사전 검증은 Application Validator, 최종 방어는 DB 제약 조건이라는 이중 방어 원칙은 채택 후보로 둔다.
예외 변환 위치는 유스케이스 맥락이 필요한 경우 Service, 저장 행위 자체의 의미가 명확한 경우 Repository 쪽으로 나눌 수 있다.

#### 하네스 반영 후보 위치

- `docs/architecture/repository-pattern.md`
- `docs/architecture/transactions.md`
- `docs/principles/exceptions.md`
- `docs/decisions/pending/db-constraint-as-final-guard.md`

---

## 이후 하네스 반영 후보

이 문서의 QnA가 어느 정도 쌓이면 아래 순서로 하네스에 반영한다.

1. 사용자 답변을 기준으로 각 항목을 채택 / 보류 / 거절로 재분류한다.
2. 반복되는 판단은 `docs/decisions/pending` 또는 `docs/decisions/accepted` 후보로 분리한다.
3. 이미 존재하는 원칙을 보강할 수 있으면 새 decision보다 기존 문서를 우선 수정한다.
4. `AGENTS.md`에는 작업자가 반드시 확인해야 하는 고수준 규칙만 반영한다.
5. 세부 구현 기준은 `docs/architecture`, `docs/principles`, `docs/workflow`에 나누어 반영한다.

---

## 현재 상태

- 상태: 초안
- 사용자 답변 수: 0
- 하네스 반영 여부: 미반영
- 다음 작업: QnA 답변을 받아 임시 판단을 갱신한다.
