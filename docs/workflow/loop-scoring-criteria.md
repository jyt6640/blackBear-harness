# Loop Scoring Criteria

점수표(05-scorecard) 각 항목의 채점 기준 상세다.
채점자가 2 / 1 / 0의 경계를 해석하지 않고 이 기준대로 판정하게 한다.

> 이 문서는 공유 정본의 채점 관점 요약이다.
> 각 항목의 철학 본문과 충돌하면 출처 정본이 우선한다.

공통 규칙:

- 2는 "위반 없음"이 아니라 "철학의 의도대로 작성됨"이다.
- 1은 위반이 국소적이고 카드 범위 안에서 수정 가능한 경우다. 근거에 위치를 적는다.
- 0은 철학의 의도 자체가 무시된 경우다.
- 판단 근거가 diff에 없으면 추측하지 말고 N/A로 두고 사유를 적는다.
- 기계가 이미 강제하는 부분(Tier 1, ArchUnit)은 green-bar 통과로 보장되므로 다시 채점하지 않는다. 각 항목 채점은 ArchUnit이 못 잡는 의미 잔여(예: S1은 레이어 위치가 아니라 "흐름과 판단이 섞였는가", S2는 "Service에 비즈니스 분기가 숨었는가")에 집중한다. → [architecture-rules-as-archunit](../decisions/accepted/architecture-rules-as-archunit.md)

---

## S1. 레이어 책임 분리와 구조 위치

출처: [layered-architecture](../architecture/layered-architecture.md), [ARCHITECTURE.md](../../ARCHITECTURE.md), [domain-first-package-structure](../decisions/accepted/domain-first-package-structure.md), [repository-interface-in-domain](../decisions/accepted/repository-interface-in-domain.md)

철학: Presentation은 HTTP 입출력, Application은 흐름 조율, Domain은 규칙,
Infrastructure는 기술 구현만 담당한다. 의존은 안쪽으로만 흐른다.
패키지는 도메인 우선으로 나누고, Repository 인터페이스는 Domain,
구현은 Infrastructure에 둔다.

- 2: 신규 / 변경 코드가 전부 자기 레이어 책임 안에 있고, 레이어를 건너뛰는 호출이 없다.
- 1: 침범이 한 곳에 국한된다. 예: Controller가 Response 조립 중 도메인 상태를 직접 비교.
- 0: Controller의 Repository 직접 호출, Infrastructure의 비즈니스 분기처럼 경계 자체가 무시됨.

전형적 위반: Controller에 DB 접근, Service에서 HTTP 객체 사용, Repository 구현에서 정책 분기.

## S2. Service는 흐름만 조율

출처: [service-orchestration-only](../decisions/accepted/service-orchestration-only.md), [small-service-method](../decisions/accepted/small-service-method.md), [domain-validation-over-getter](../decisions/accepted/domain-validation-over-getter.md), [policy-object-separation](../decisions/accepted/policy-object-separation.md)

철학: Service는 Domain / Policy / Validator / Repository 호출 순서만 조율한다.
판단(조건 분기로 표현되는 비즈니스 규칙)은 Service에 두지 않는다.

- 2: Service 메서드가 호출 나열로 읽힌다. 비즈니스 의미의 if / 계산이 없다.
- 1: 판단 한두 개가 Service에 남아 있으나 Domain / Policy로 옮길 자리가 명확하다.
- 0: 비즈니스 규칙의 본체(상태 판정, 금액 계산, 권한 판단)가 Service에 있다.

전형적 위반: `if (order.getStatus() == ...)` 식 상태 판단, Service 안의 검증 로직, getter로 꺼내 비교.

## S3. 검증 책임 위치

출처: [domain-boundary](../architecture/domain-boundary.md), [util-based-validation](../decisions/rejected/util-based-validation.md)

철학: null / blank / 형식은 Request DTO, 자기 상태 기반 규칙은 Domain / Policy,
저장소 조회 기반 검증(중복, 존재)은 Application Validator에 둔다.

- 2: 세 종류의 검증이 각자 제자리에 있다.
- 1: 위치가 한 종류 어긋났으나 검증 자체는 존재한다. 예: blank 검증이 Domain에 중복.
- 0: 검증이 Service / Controller에 누적되거나, Util 클래스로 빠졌거나, 아예 없다.

전형적 위반: Service의 null 체크 누적, Controller의 중복 검사, ValidationUtil.

## S4. Domain의 기술 독립

출처: [domain-does-not-know-technology](../decisions/accepted/domain-does-not-know-technology.md)

철학: Domain은 저장 기술, 프레임워크, 외부 시스템을 알지 못한다.

- 2: Domain 패키지에 기술 import / 어노테이션 / 외부 호출이 없다.
- 1: 기술 흔적이 있으나 프로젝트 decision으로 허용된 범위다 (예: 통합 구조 decision의 영속화 필드).
- 0: Domain이 HTTP / 외부 API / 저장 기술 동작에 의존해 규칙을 판단한다.

전형적 위반: Domain에서 RestTemplate 호출, Domain 메서드가 영속화 상태를 전제로 동작.

## S5. 생성 경로 통제

출처: [static-factory-method](../decisions/accepted/static-factory-method.md)

철학: Domain Entity / Aggregate 생성자는 닫고 `create`(신규) / `restore`(복원) / `of`(값)로
생성 의도를 구분한다. 생성 시 검증은 생성 경로 안에 있다.

- 2: 생성자 private, 의도별 정적 팩터리, 생성 검증이 팩터리 안에 있다.
- 1: 팩터리는 있으나 의도 구분이 없거나(모두 of), 생성 검증 일부가 밖에 있다.
- 0: public 생성자로 어디서든 생성 가능하거나, 검증 없는 생성 경로가 있다.

전형적 위반: public 생성자 + setter 조합, 테스트에서만 쓰는 우회 생성 경로.

## S6. Request / Command 분리

출처: [request-command-separation](../decisions/accepted/request-command-separation.md)

철학: HTTP 스펙 변경과 비즈니스 요구 변경은 변경 이유가 다르다.
Service 입력은 Request DTO가 아니라 Command / Query다.

- 2: Controller가 Request를 Command로 변환해 전달하고, Service 시그니처에 Request가 없다.
- 1: 분리는 됐으나 변환이 Service 안에서 일어나는 등 위치가 어긋남.
- 0: Request DTO가 Service / Domain까지 그대로 흘러 들어간다.

## S7. 예외의 의미 변환과 위치

출처: [infrastructure-exception-translation](../decisions/accepted/infrastructure-exception-translation.md), [exceptions](../principles/exceptions.md), [exception-hierarchy](../decisions/accepted/exception-hierarchy.md)

철학: 예외를 던지는 위치는 검증 책임 위치와 일치한다. 기술 예외는 Infrastructure가
저장소 의미 예외로, Application이 필요한 경우 유스케이스 의미(ErrorCode)로 변환한다.
커스텀 예외는 unchecked다.

- 2: 예외가 책임 위치에서 도메인 의미로 던져지고, 기술 예외가 Application에 노출되지 않는다
  (프로젝트 decision으로 전파를 허용한 경우는 그 기준을 따른다).
- 1: 의미 변환은 있으나 위치가 어긋나거나, ErrorCode 없이 메시지 문자열로만 구분.
- 0: `throw new RuntimeException("...")`, Application의 구체 기술 예외 세부 해석.

## S8. 트랜잭션 경계 위치와 후속 작업 분리

출처: [transaction-boundary-in-service](../decisions/accepted/transaction-boundary-in-service.md), [follow-up-failure-boundary](../decisions/accepted/follow-up-failure-boundary.md), [transactions](../architecture/transactions.md)

철학: 트랜잭션 경계는 Application Service에 둔다. 후속 작업(알림, 적립 등)의
실패가 원 작업을 실패시킬지는 코드 묶음이 아니라 사용자 성공 기준으로 판단하고,
분리하더라도 조용히 무시하지 않고 로그와 복구 경로를 둔다.

- 2: 경계가 Service에 있고, 후속 작업의 성공 관계가 요구사항 기준으로 판단되어 있다.
  분리된 후속 작업에는 식별자가 포함된 로그 / 복구 경로가 있다.
- 1: 경계는 맞으나 후속 작업 실패 처리가 로그 없이 삼켜지는 곳이 한 군데 있다.
- 0: Controller / Repository에서 트랜잭션 시작, 또는 후속 작업 실패가 원 작업을
  근거 없이 함께 실패시키거나 무단으로 무시된다.

전형적 위반: catch 후 빈 블록, 알림 실패로 주문 취소 rollback, 요구사항 확인 없는 분리 판단.

## S9. 도메인 간 협력

출처: [domain-reference-adapter](../decisions/accepted/domain-reference-adapter.md), [domain-boundary](../architecture/domain-boundary.md)

철학: 도메인은 협력하되 다른 도메인의 책임을 대신하지 않는다.
다른 도메인과의 상호작용은 Reference 포트(인터페이스는 필요한 쪽 application,
Adapter는 협력 수단을 가진 쪽)로 캡슐화하고, 상대 도메인 객체를 노출하기보다
필요한 사실 / 행위만 제공한다.

- 2: 도메인 간 협력이 Reference 포트로 캡슐화되어 있고, 상대 도메인의 상태를
  직접 조작하는 곳이 없다.
- 1: 포트 구조는 있으나 상대 도메인 객체가 그대로 노출되는 곳이 한 군데 있다.
- 0: Service가 상대 도메인 Repository를 직접 조회해 내부 상태를 판단하거나,
  한 도메인이 다른 도메인의 상태를 직접 변경한다.

전형적 위반: OrderService가 MemberRepository 직접 주입, Product가 Order 상태 변경.

## S10. 접근 권한 판단 위치

출처: [authorization-policy-placement](../decisions/accepted/authorization-policy-placement.md), [domain-boundary](../architecture/domain-boundary.md)

철학: 접근 권한 판단(소유권·가시성·관계)은 Policy 또는 도메인 행위가 소유한다.
Service에 권한 분기를 두지 않는다. 규칙이 도메인 상태 + 저장소 조회를 함께
필요로 하면, 도메인이 규칙을 갖고 외부 사실(친구 여부 등)을 인자로 받는다
(Application이 Reference 포트로 조회해 주입). 도메인은 Repository를 모른다.

- 2: 권한 규칙이 도메인 행위 / Policy에 응집되어 있고, Service 본문에는 조회 →
  권한 확인 호출 → 처리만 보인다. 외부 사실은 인자로 주입된다.
- 1: 규칙은 Policy에 있으나 guard 한두 개가 Service에 남거나, 도메인이 외부 사실을
  인자로 받지 않고 getter 비교가 한 곳 남는다.
- 0: 권한 분기(owner/visibility/participant/관계)가 Service의 if 체인으로 흩어져 있다.

전형적 위반: Service가 schedule.getVisibility() 등을 꺼내 PUBLIC/FRIENDS_ONLY를
직접 분기하고 끝에 ForbiddenException을 던진다.

## T1. public behavior 단위 직접 테스트

출처: [public-behavior-based-tdd](../decisions/accepted/public-behavior-based-tdd.md), [testing](../principles/testing.md)

철학: 테스트 단위는 기능명이 아니라 production class의 public behavior다.
Domain / Policy / Validator의 판단은 해당 class 테스트에서 직접 검증하고,
Service / 통합 테스트가 이를 대체하지 않는다. private 메서드는 직접 테스트하지 않는다.

- 2: 변경된 모든 production class에 행위 단위 직접 테스트가 있고, 성공 / 실패 케이스가 분리된다.
- 1: 직접 테스트는 있으나 일부 책임이 Service 테스트에 묻혀 있다.
- 0: 통합 / Service 테스트만으로 덮였거나, private 메서드를 직접 테스트한다.

## T2. 테스트 더블을 책임 기준으로 선택

출처: [test-double-by-responsibility](../decisions/accepted/test-double-by-responsibility.md)

철학: 도구 선호가 아니라 테스트 대상 책임으로 고른다.
Service(흐름·협력 호출)=Mock, Validator(상태 기반 검증)=Fake 우선, Controller=Mock.

- 2: 더블 선택이 전부 책임 기준과 일치하고, Fake는 fake 패키지에 분리되어 있다.
- 1: 한두 곳이 어긋나거나(Validator에 Mock), Fake가 테스트 내부 클래스다.
- 0: 일괄 Mock 또는 일괄 Fake로 책임 구분 없이 사용.

## T3. 실패 확인 선행

출처: [tdd](./tdd.md)

철학: 실패하는 테스트를 실행으로 확인한 뒤 구현한다. 실패 원인은
기대 행위 부재여야 한다 (컴파일 오류 / 환경 문제가 아니라).

- 2: 01-test-report에 실행 로그 기반 실패 확인이 있고, 원인 분류가 "기대 행위 미구현"이다.
- 1: 실패 확인은 있으나 원인 분류가 없거나 로그 근거가 약하다.
- 0: 실패 확인 없이 구현이 시작됐다 (보고서에 근거 부재).

## T4. 동시성 테스트 경계

출처: [concurrency-test-boundary](../decisions/accepted/concurrency-test-boundary.md)

철학: 동시 쓰기 경쟁(중복 생성, 재고 차감, 결제 승인 등)이 있는 유스케이스는
Mock / Fake로 검증할 수 없다. 실제 Spring Context와 DB를 사용하는 통합 테스트로
최종 상태를 검증한다. 이 원칙이 일반 Service 테스트를 @SpringBootTest로 만드는
근거가 되어서는 안 된다.

- 2: 동시성 위험 유스케이스에 실제 환경 기반 동시성 테스트가 있고, 최종 상태를 검증한다.
- 1: 동시성 테스트는 있으나 호출 여부만 검증하거나 재현 안정성 장치가 없다.
- 0: 동시성 위험이 카드에 명시됐는데 테스트가 없거나 Mock / Fake로 대체했다.
- N/A: 카드에 동시 쓰기 경쟁 유스케이스가 없다.

## R1. 의도가 드러나는 네이밍

출처: [naming](../principles/naming.md), [common-util-package](../decisions/rejected/common-util-package.md)

철학: 이름은 도메인 모델링의 결과다. 역할이 드러나는 이름(Policy / Validator / Command)을
쓰고, Manager / Helper / Util 같은 책임 불명 이름을 만들지 않는다.

- 2: 신규 클래스 / 메서드 이름만으로 의도가 읽히고, 도메인 용어를 쓴다.
- 1: 이름 한두 개가 구현을 설명하거나(processData) 모호하지만 책임 자체는 분명하다.
- 0: Helper / Util / Manager 신설, 이름과 실제 책임의 불일치.

## R2. 메서드 설계

출처: [method-design](../principles/method-design.md)

철학: 한 메서드는 하나의 의도만 표현한다. else보다 early return / throw,
boolean 파라미터로 정책 차이를 숨기지 않는다.

- 2: 메서드가 한 의도로 읽히고, 분기가 early return으로 정리되어 있다.
- 1: 의도 혼합이 한 곳 있거나(이름에 and), 깊은 중첩이 남아 있다.
- 0: 한 메서드가 검증 + 판단 + 저장을 모두 수행, boolean 플래그로 동작 분기.

## R3. Lombok 사용 기준

출처: [lombok-usage-guideline](../decisions/accepted/lombok-usage-guideline.md), [lombok](../principles/lombok.md)

철학: Lombok은 보일러플레이트 절감 도구이지 객체 설계를 대체하지 않는다.
객체의 생성 경로와 상태 변경 권한을 외부에 열면 안 되며,
Domain / Persistence Entity / DTO의 역할에 따라 허용 범위가 다르다.

- 2: Lombok 사용이 역할별 기준 안에 있고, Domain의 생성 / 변경 경로가 닫혀 있다.
- 1: 허용 범위 밖 어노테이션이 한 곳 있으나 경로 통제는 유지된다 (예: DTO 외 @Builder).
- 0: Domain에 @Setter / @AllArgsConstructor(public)로 생성·변경 경로가 열렸다.
- N/A: 이 카드의 diff에 Lombok 사용이 없다.

전형적 위반: Domain Entity의 @Data, @Setter, public @Builder로 검증 우회 생성.

## P1. 커밋 단위와 순서

출처: [git-convention](./git-convention.md)

철학: 한 커밋은 하나의 public behavior 또는 하나의 책임 변경만 포함하고,
test → feat → refactor 순서를 지키며 각 단계는 자기 type만 만든다.
형식과 순서는 스크립트가 검사하므로 여기서는 행위/책임 단위를 본다.

- 2: 행위 하나당 커밋 하나가 지켜졌고, 보고서의 커밋 목록과 이력이 일치한다.
- 1: 행위 두 개가 한 커밋에 묶인 곳이 한 번 있다.
- 0: 단계 산출물 전체가 커밋 한 개이거나, 커밋 목록과 이력이 불일치.

## P2. 카드 범위 준수

출처: 불변 철학(최소 변경), [explicit-over-reuse](../decisions/accepted/explicit-over-reuse.md)

철학: 현재 요구사항 해결에 필요한 범위만 최소 변경한다. 관련 없는 리팩터링,
근거 없는 새 패턴 / 라이브러리, 미래를 위한 선제 추상화를 하지 않는다.

- 2: diff가 카드의 대상 행위 / 범위 안에 있다.
- 1: 범위 밖 변경이 있으나 기계적(이동에 따른 import 등)이고 사유가 보고서에 있다.
- 0: 카드에 없는 기능 / 추상화 / 의존성이 추가됐다.
