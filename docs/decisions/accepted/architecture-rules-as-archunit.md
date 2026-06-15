# Architecture Rules As ArchUnit

## 상태

accepted

---

## 문제 상황

`docs/architecture`와 `docs/principles`는 이 하네스의 근간 철학이지만,
산문(prose)이라 스스로 강제력이 없었다. 모델이 따를 수도, 안 따를 수도 있었다.

강제 수단으로 `scripts/check-philosophy.sh`(grep)를 먼저 도입했으나 세 결함이 있었다.

- 오탐: `code` 같은 식별자에 BLOCK이 걸렸다.
- 우선순위 역전: base의 grep BLOCK이 프로젝트 production code 의도(판단 1순위)를
  0순위에서 짓밟았다.
- 프로젝트 override 불가: base 스크립트의 BLOCK을 프로젝트가 끌 수단이 없었다
  (프로젝트 훅은 검사를 add만 한다).

근간 철학에 강제력을 주려면, 문장을 강제 가능성으로 분류해야 했다.

- Tier 1: AST로 판정 가능한 구조 규칙 (의존방향, 패키지, 어노테이션 위치 등)
- Tier 2: 스타일 (삼항/else/메서드 길이) — 취향, 문서도 "강박 두지 않음"이라 명시
- Tier 3: 의미 규칙 (Service에 비즈니스 판단, Tell-Don't-Ask, 최소 변경) — 기계 불가

선택지:

- A. grep 확장 — 오탐·우선순위 역전·override 불가가 그대로 남는다.
- B. ArchUnit 테스트 — Tier 1을 AST로 정확히 판정하고, 프로젝트 test source에 산다.

---

## 선택한 방향

Tier 1 구조 규칙은 **ArchUnit 테스트**로 강제하고, 프로젝트 `verify.sh`
green-bar에 포함한다. 규칙을 어기는 코드는 빌드를 통과할 수 없다.

- 정본 템플릿: `scripts/project-templates/archunit/`
  - `ArchitectureTest.java`: 의존방향·Domain순수성·생성/상태·트랜잭션/예외·패키지/네이밍
  - `ProductionClassTestCoverageTest.java`: 모든 production class 직접 테스트
  - `AggregateRoot.java`: Entity/Aggregate 식별 마커 (생성자 private 규칙 대상)
- 프로젝트는 템플릿을 복사해 루트 패키지명을 채운다.
- 규칙을 끄려면 해당 `@ArchTest` 필드를 지운다. base 파일은 건드리지 않는다.

강제 대상(Tier 1):

- 레이어 의존 방향 / 레이어 스킵 금지 → [layered-architecture](../../architecture/layered-architecture.md)
- Domain의 기술(JPA/Servlet/Spring)·Infra·Presentation 의존 금지
- Domain setter / @Setter / @Data 금지 → [oop](../../principles/oop.md), [lombok](../../principles/lombok.md)
- @AggregateRoot 생성자 private → [static-factory-method](./static-factory-method.md)
- @Transactional은 Application에만 → [transaction-boundary-in-service](./transaction-boundary-in-service.md)
- 예외 핸들러는 global에만 / 커스텀 예외 RuntimeException 기반 → [exception-hierarchy](./exception-hierarchy.md)
- Request/Response→presentation/dto, Command/Query→application/dto
- Repository 인터페이스 in domain → [repository-interface-in-domain](./repository-interface-in-domain.md)
- Util/Helper/Manager 클래스명 금지 → [naming](../../principles/naming.md)
- 모든 production class 직접 테스트 → [public-behavior-based-tdd](./public-behavior-based-tdd.md)

Tier 2(스타일)는 ArchUnit에 두지 않는다. Tier 3(의미)은 Review + 점수표가 맡는다.

---

## 선택 이유

### 우선순위 역전이 사라진다

ArchUnit 규칙은 프로젝트 test source에 살고 `verify.sh`에 포함된다.
판단 1순위인 production code와 **같은 자리**에서 잰다. base grep이 코드 의도를
바깥에서 짓밟던 구조가 없어진다.

### 정확하다

문자열 grep과 달리 타입·패키지·의존을 AST로 본다. `code` 식별자 오탐 같은 일이
없다.

### 프로젝트 override가 자연스럽다

규칙을 끄려면 `@ArchTest` 필드를 지우면 끝이다. base/프로젝트 분리를 깨지 않는다.
이는 [enforcement-by-script](./enforcement-by-script.md)의 "기계 판정 가능한 규칙은
스크립트로 강제"를 grep보다 정확한 수단으로 잇는다.

### grep의 결함 항목을 흡수한다

[mechanical-philosophy-block](./mechanical-philosophy-block.md)의 구조 검사
(책임불명 클래스명, Domain setter, global 밖 핸들러, DTO 위치, RuntimeException 등)을
ArchUnit이 정확히 대체한다.

---

## 트레이드오프

### 패키지 관례 의존

규칙은 `package-structure.md`의 레이아웃(`domain`/`application`/`presentation`/
`infrastructure`/`global`)을 전제한다. 관례가 다른 프로젝트는 패키지 식별자를 조정한다.

### 마커 도입 비용

Entity/Aggregate 생성자 private 규칙은 `@AggregateRoot` 마커를 Entity에 붙여야
발효한다. VO와 Entity를 패키지만으로 못 가르기 때문에 감수하는 비용이다.

### Java 빌드 전 차단은 못 한다

ArchUnit은 컴파일된 클래스를 본다. 빌드 이전 빠른 1차 차단이 필요하면 grep 훅을
보조로 남길 수 있으나, 완료 판정의 정본은 ArchUnit green-bar다.

---

## 현재 판단

근간 철학 중 기계화 가능한 Tier 1은 ArchUnit으로 강제해 "절대 못 어김"으로 만든다.
Tier 2/3는 각각 린터와 Review/점수표가 맡는다. grep `check-philosophy.sh`의 구조
검사는 ArchUnit으로 대체되며, 남길지 여부는 프로젝트가 정한다.

이 결정은 [enforcement-by-script](./enforcement-by-script.md)와
[project-verify-green-bar](./project-verify-green-bar.md)를 잇고,
[mechanical-philosophy-block](./mechanical-philosophy-block.md)의 구조 검사 범위를
ArchUnit으로 이관한다.

---

## 재검토 신호

- ArchUnit 규칙이 정당한 코드를 반복 오탐한다 (규칙 조정).
- 패키지 관례가 전제와 달라 규칙 다수가 무의미하다.
- 점수표(Tier 3)에서 반복 감점되는 항목 중 AST로 기계화 가능한 것이 나온다 (규칙 추가).
- `@AggregateRoot` 마커 운영 비용이 효익을 넘는다 (네이밍 규약으로 전환 검토).
