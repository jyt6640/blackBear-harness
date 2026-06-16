# Feat Agent Workflow

> 역할 실행 문서다. 철학 본문은 여기 쓰지 않는다.
> 관련 철학·판단 기준은 docs/principles/, docs/workflow/를 참조한다.


Feat Agent의 workflow는 `01-red-test-report.md`의 실패 테스트를 작업 카드 범위 안에서 통과시키는 절차다.
새 요구사항을 해석해 넓히는 단계가 아니라, 이미 고정된 행위 계약을 최소 production code로 만족시키는 단계다.

---

## 시작 순서

1. `agents/feat/scripts/enforce-workflow.sh <작업명>`을 실행한다.
2. `next-step/work/<작업명>/00-task-card.md`와 `01-red-test-report.md`를 읽는다.
3. 실패 테스트를 실행해 현재 Red 상태를 재확인한다.
4. 변경할 production class와 책임 위치를 정한다.
5. 하나의 실패 행위씩 최소 구현으로 Green을 만든다.

`01-red-test-report.md`가 없거나 실패 테스트가 재현되지 않으면 구현을 시작하지 않는다.

---

## 구현 순서

기본 구현 순서는 Test Agent가 만든 테스트 순서를 따른다.

1. Domain
2. Application Validator
3. Service
4. Repository
5. Controller
6. Acceptance 흐름 보정

Controller부터 구현하지 않는다.
Service에 정책 판단을 몰아넣지 않는다.
안쪽 책임이 확정된 뒤 바깥 레이어를 연결한다.

---

## 레이어별 방식

### Domain

- 핵심 비즈니스 규칙과 상태 변경을 둔다.
- 생성자는 닫고 정적 팩터리 메서드를 기본으로 한다.
- getter를 꺼내 외부에서 판단하게 만들지 않는다.

### Application Validator

- Repository 조회가 필요한 검증 책임을 둔다.
- 저장소 상태 기반 실패 케이스를 명확히 표현한다.
- Service 테스트가 Validator 판단을 대신하지 않게 한다.

### Service

- 유스케이스 흐름, 객체 협력, 트랜잭션 경계를 조율한다.
- 정책 판단은 Domain / Policy / Validator에 위임한다.
- 입력은 Request DTO가 아니라 Command로 받는다.

### Repository

- 저장 기술 구현, SQL, 매핑을 담당한다.
- 기술 예외는 Infrastructure에서 저장소 의미 예외로 변환한다.
- 비즈니스 정책 판단을 넣지 않는다.

### Controller

- HTTP 요청/응답, DTO 변환, 상태 코드만 담당한다.
- Request DTO에서 HTTP 입력 필수값을 검증한다.
- Controller가 DB 접근이나 비즈니스 판단을 직접 수행하지 않는다.

---

## 구현 컨벤션

- 테스트 수정 없이 production code만 변경한다.
- 작업 카드에 없는 행위는 추가하지 않는다.
- 현재 코드에 없는 패턴, 라이브러리, 프레임워크를 도입하지 않는다.
- 관련 없는 리팩터링은 하지 않는다.
- 이름은 역할을 드러내게 짓고 `Helper`, `Util`, `Manager`를 피한다.
- 공통화는 필요성이 증명된 뒤 도입한다.
- 한 번에 여러 실패를 크게 처리하지 않고 public behavior 단위로 진행한다.

테스트가 잘못됐다고 판단되면 직접 수정하지 않는다.
`02-green-implementation-report.md`에 모순을 기록하고 오케스트레이터에게 되돌린다.

---

## 검증 순서

1. 대상 실패 테스트를 실행한다.
2. 관련 단위 테스트를 실행한다.
3. 변경 범위가 넓으면 레이어별 테스트 또는 전체 테스트를 실행한다.
4. 테스트 통과 후 리팩터링 필요 신호를 기록한다.

Feat 단계에서 즉시 리팩터링하지 않는다.
행위 변경 없는 정리가 필요하면 Refactor 단계 또는 별도 작업 카드로 분리한다.

---

## 보고서 작성

`next-step/work/<작업명>/02-green-implementation-report.md`에 아래를 기록한다.

- 통과시킨 실패 테스트
- 실행한 검증 명령
- 변경한 production 파일
- 레이어별 책임 배치
- 테스트를 수정하지 않았는지 여부
- 남은 리팩터링 신호 또는 Review 요청 사항

보고서는 Review Agent의 입력이다.
구현 코드만 있고 보고서가 없으면 Review 단계로 넘길 수 없다.
