# Harness Interview

이 문서는 프로젝트 하네스를 세울 때 AI 작업자가 진행하는 인터뷰 절차를 정의한다.

목적: 프로젝트 시작 또는 기존 코드 분석 단계에서
base 하네스의 비워진 슬롯을 질문으로 채우고,
답을 decision으로 기록해 팀과 공유 가능한 개발 하네스를 만드는 것이다.

전체 절차의 맥락은 [how-to-create-project-harness.md](./how-to-create-project-harness.md)를 따른다.

---

## 핵심 방향

- AI 작업자가 먼저 인터뷰를 제안한다. 사람이 요청하기를 기다리지 않는다.
- 규모에 따라 질문 세트를 조절한다. 작은 프로젝트에 모든 질문을 쏟지 않는다.
- 모든 답은 decision으로 기록한다. 대화로만 남기지 않는다.
- 답이 나오지 않은 질문은 pending으로 남기고, 해당 영역 작업 시 다시 질문한다.

---

## 시작 조건

아래 상황이면 구현을 시작하기 전에 인터뷰를 제안한다.

- 새 프로젝트의 첫 작업 요청을 받았는데 프로젝트 decisions가 없다.
- 기존 코드의 분석 / 리뷰 / 기능 추가를 요청받았는데 프로젝트 하네스가 없다.
- 같은 상위 전제 질문이 두 번 이상 반복되고 있다.

인터뷰는 강제가 아니다.
사람이 지금은 넘어가자고 하면 진행하되,
답이 필요한 영역에 도달할 때마다 개별 질문으로 돌아온다.

---

## 1단계: 규모 파악

먼저 아래 다섯 가지를 묻는다. 답이 이후 질문의 깊이를 결정한다.

팀 인원이 아니라 사용자 규모가 분기 기준이다.
사용자 규모가 동시성, 인증, 성능, 운영 요구를 결정하기 때문이다.

1. 예상 사용자 규모와 트래픽은? (내부 도구 / 소수 사용자 | 일반 서비스 | 대규모·성장 예상)
2. 예상 수명은? (과제 / 프로토타입 | 운영 목표 | 장기 운영·확장)
3. 기술 스택(DB, 프레임워크)은 고정인가, 교체 가능성이 있는가?
4. 동시 쓰기 경쟁이 있는 유스케이스가 있는가? (재고, 선착순, 중복 방지 등)
5. 배포 형태는? (모놀리스 | 분리 계획 있음 | MSA)

### 규모 프로파일

| 프로파일 | 신호 | 질문 범위 |
|---|---|---|
| 소형 | 내부 도구·소수 사용자, 과제/프로토타입, 기술 고정 | 필수 영역만, 나머지는 base 일괄 수용 확인 |
| 중형 | 일반 서비스 규모, 운영 목표 | 필수 + 구조·책임·컨벤션 영역 |
| 대형 | 대규모 트래픽, 장기 운영, 교체·확장 가능성, MSA | 전체 영역 + base 기본 입장 override 검토 |

프로파일이 애매하면 작은 쪽으로 시작하고,
재검토 신호가 나타나면 상위 프로파일의 질문으로 돌아온다.

---

## 2단계: 결정 영역 질문

인터뷰는 아래 영역을 채운다.
각 질문은 답이 기록될 decision과 연결되고,
영역별 깊이는 1단계 프로파일에 따라 달라진다.

해당 없는 질문은 건너뛰지 말고 not-applicable로 기록한다.
모르는 것(pending)과 필요 없는 것(not-applicable)은 다르다.

기록 위치의 링크 없는 이름(예: test-strategy)은
프로젝트 decisions에 만들 권장 파일명이다.

### 영역 1: 기본 기술 스택 (모든 규모)

| 질문 | 기록 위치 |
|---|---|
| 언어 / 프레임워크 / DB / 인프라는 무엇인가? | 프로젝트 사실 |
| 이 스택은 고정인가, 교체 가능성이 있는가? | [spring-dao-exception-propagation](../decisions/pending/spring-dao-exception-propagation.md), [repository-exception-contract](../decisions/pending/repository-exception-contract.md) |

기술 스택은 선정 시점의 결정이다.
개발 중 새 기술이 추가될 수 있으며, 추가할 때마다 draft decision으로 기록한다.

### 영역 2: 영속성 컨텍스트 (모든 규모)

| 질문 | 기록 위치 |
|---|---|
| 영속성 기술(JPA / JDBC / MyBatis 등)은 무엇인가? | 프로젝트 사실 |
| Domain과 Persistence Entity를 분리하는가? Domain이 영속성 기술에 의존해도 되는가? | [domain-entity-separation](../decisions/pending/domain-entity-separation.md) |
| 저장소 구현체의 기술 예외를 Application까지 전파하는가? Spring 기반이면 DataAccessException 전파 허용 여부를 함께 정한다. | [spring-dao-exception-propagation](../decisions/pending/spring-dao-exception-propagation.md) |
| Repository 인터페이스 계약에 저장소 의미 예외를 드러내는가? | [repository-exception-contract](../decisions/pending/repository-exception-contract.md) |

### 영역 3: 아키텍처 구조 (모든 규모, 깊이는 프로파일별)

| 질문 | 기록 위치 |
|---|---|
| 패키지 구조는 base 기본 입장(도메인 우선)을 따르는가? | [domain-first-package-structure](../decisions/accepted/domain-first-package-structure.md) override 여부 |
| 도메인 간 협력에 이벤트 기반 구조를 도입하는가? (중형 이상) | [event-driven-boundary](../decisions/pending/event-driven-boundary.md) |
| Aggregate 경계와 강한 일관성 범위 기준은? (대형) | [aggregate-boundary](../decisions/pending/aggregate-boundary.md) |

### 영역 4: 레이어드 책임 (중형 이상, 소형은 base 일괄 수용 확인만)

| 질문 | 기록 위치 |
|---|---|
| base의 레이어 책임 분리(Presentation / Application / Domain / Infrastructure)를 그대로 쓰는가? | [ARCHITECTURE.md](../../ARCHITECTURE.md) override 여부 |
| 검증 책임 배치(Domain Policy / Application Validator)와 Validator 위치는? | [validator-package-location](../decisions/pending/validator-package-location.md) |

### 영역 5: 트랜잭션 정책 (중형 이상, 소형은 base 일괄 수용 확인만)

| 질문 | 기록 위치 |
|---|---|
| 트랜잭션은 base 기본 입장대로 Application Service에서 시작하는가? 다른 위치의 트랜잭션 시작을 허용하는가? | [transaction-boundary-in-service](../decisions/accepted/transaction-boundary-in-service.md) override 여부 |
| 읽기 트랜잭션에 readOnly를 기본으로 쓰는가? | transaction-policy |
| 외부 API 호출을 트랜잭션 안에서 허용하는가? | external-call-transaction-policy |

### 영역 6: API 응답 / 에러 정책 (모든 규모)

| 질문 | 기록 위치 |
|---|---|
| 성공 응답에 공통 포맷을 쓰는가? base는 generic response wrapper를 피하는 입장이다. | [generic-response-wrapper](../decisions/rejected/generic-response-wrapper.md) override 여부, api-response-format |
| 에러 응답 필드는 어떻게 구성하는가? | error-response-policy |
| validation error는 어떤 형태로 내려주는가? | validation-error-format |
| HTTP status와 business error code 기준은 어떻게 나누는가? | [exception-hierarchy](../decisions/accepted/exception-hierarchy.md) 구체화, error-response-policy |

### 영역 7: DB 제약 / 데이터 정책 (중형 이상, 동시 쓰기 경쟁이 있으면 모든 규모)

| 질문 | 기록 위치 |
|---|---|
| 중복 방지를 애플리케이션 검증으로 처리하고, DB unique constraint를 최종 방어선으로 두는가? | [infrastructure-exception-translation](../decisions/accepted/infrastructure-exception-translation.md) 전제 확인, unique-constraint-policy |
| FK를 적극적으로 쓰는가? | database-constraint-policy |
| soft delete를 쓰는가? | delete-policy |

### 영역 8: 테스트 정책 (모든 규모, 소형은 base 일괄 수용 확인만)

| 질문 | 기록 위치 |
|---|---|
| 테스트 더블은 base 기본 입장(책임별 Mock / Fake 선택)을 따르는가? | [test-double-by-responsibility](../decisions/accepted/test-double-by-responsibility.md) override 여부 |
| Controller / Repository 테스트 도구는 무엇인가? (MockMvc / RestAssured, @DataJpaTest / @JdbcTest 등) | test-strategy |
| 통합 테스트 범위는 어디까지인가? | test-strategy |
| 동시 쓰기 경쟁 유스케이스가 있다면, 동시성 테스트는 언제 작성하는가? 없다면 not-applicable로 기록한다. | [concurrency-test-boundary](../decisions/accepted/concurrency-test-boundary.md) 적용, concurrency-test-policy |

### 영역 9: 코드 컨벤션 (모든 규모, 소형은 base 일괄 수용 확인만)

| 질문 | 기록 위치 |
|---|---|
| 네이밍 / 커밋 컨벤션 중 base와 다르게 갈 부분이 있는가? | 해당 base 문서 override decision |
| Lombok 사용 기준은 base를 따르는가? | [lombok-usage-guideline](../decisions/accepted/lombok-usage-guideline.md) override 여부 |
| Fake 위치 같은 테스트 컨벤션은? | [fake-package-location](../decisions/pending/fake-package-location.md) |

### 영역 10: 로깅 / 관찰 정책 (중형 이상)

| 질문 | 기록 위치 |
|---|---|
| 어떤 예외를 warn / error로 남기는가? 사용자 입력 오류는 로그를 남기는가? | logging-policy |
| 후속 작업 실패 로그에는 어떤 식별자를 남기는가? | [follow-up-failure-boundary](../decisions/accepted/follow-up-failure-boundary.md)의 로그 맥락 구체화 |
| traceId / requestId를 사용하는가? | failure-observability-policy |

### 영역 11: 협업 정책 (팀 작업이면 모든 규모)

| 질문 | 기록 위치 |
|---|---|
| PR은 어떤 단위로 나누는가? 리뷰어는 무엇을 우선 검토하는가? | [git-convention](./git-convention.md), [code-review](./code-review.md) override 여부 |
| decision 변경이 필요한 PR은 어떻게 표시하는가? | decision-change-policy |
| 코드 스타일 논쟁은 어디서 결정하는가? | decision 절차로 합의 → [decisions/README](../decisions/README.md) |

### 영역 12: 사용자 규모 파생 결정 (1단계 답에 따라)

| 질문 | 기록 위치 |
|---|---|
| 인증 / 권한 요구가 있는가? 어떤 방식인가? | [security-auth-pattern](../decisions/pending/security-auth-pattern.md) |
| 동시 쓰기 경쟁 유스케이스가 있다면, 최종 일관성 기준은 무엇인가? 없다면 not-applicable로 기록한다. | 프로젝트 decision (동시성 제어 방식) |
| 후속 작업(알림, 적립 등) 실패가 원 작업을 실패시켜야 하는가? 재시도 인프라는 무엇인가? | [follow-up-failure-boundary](../decisions/accepted/follow-up-failure-boundary.md)의 운영 전제 |

### 질문 방식

- 질문에는 왜 묻는지와 선택지의 트레이드오프를 함께 제시한다.
- base에 잠정 기준이 있으면 그것을 기본값으로 제안한다.
- 사람이 모르겠다고 답하면 base 잠정 기준을 설명하고 pending으로 둔다.

---

## 3단계: 기존 코드가 있는 경우

질문하기 전에 production code, 문서, git 기록을 먼저 관찰한다.

- 코드에서 답이 보이는 질문은 열린 질문 대신 확인 질문으로 바꾼다.
  "코드를 보니 Domain과 Entity가 통합 구조로 보입니다(Order.java). 이대로 확정할까요?"
- 관찰 근거(파일 경로, 코드 위치)를 함께 제시한다.
- 코드끼리 일관되지 않은 부분은 열린 질문으로 묻는다.
- 관찰만으로 accepted를 만들지 않는다. 관찰은 draft, 결정은 사람 확인 후다.

---

## 4단계: decision 기록

- 각 답을 [TEMPLATE](../decisions/TEMPLATE.md) 형식의 draft decision으로 작성한다.
- draft에는 작성 계기(인터뷰 답변 / 코드 관찰)와 근거를 남긴다.
- 사람이 확인하면 accepted로 옮긴다.
- 보류된 질문은 pending으로 기록하고 재검토 신호를 적는다.
- 해당 없다고 확인된 질문은 not-applicable로 기록한다.
  이유와 다시 해당하게 되는 재검토 신호를 짧게 남겨, 같은 질문이 반복되지 않게 한다.

---

## 산출물

인터뷰가 끝나면 아래가 만들어져 있어야 한다.

- 프로젝트 AGENTS.md — AI 작업자가 따를 프로젝트 하네스. base 선언과 diff만 기록
- 프로젝트 decisions/ — 왜 그렇게 결정했는지. accepted(확정), pending(보류), not-applicable(해당 없음)
- development-guideline.md — 팀원이 바로 따를 개발 규칙. 이유 없이 규칙만 간결하게
- 팀 공유 요약 — 결정 목록과 이유를 한 장으로 정리 (선택)

역할이 겹치지 않게 분리한다.

| 산출물 | 독자 | 내용 |
|---|---|---|
| AGENTS.md | AI 작업자 | base 하네스와의 diff |
| decisions/ | 결정을 검토하는 사람 | 왜 (이유, 트레이드오프, 재검토 신호) |
| development-guideline.md | 팀원 | 무엇을 (바로 따를 규칙, decision 링크) |

development-guideline.md의 각 규칙은 근거 decision을 링크한다.
규칙과 이유가 한 문서에 섞이면 어느 쪽도 읽기 어려워진다.

이 산출물이 팀원과 나누는 개발 하네스다.
팀원의 이견은 decision 수정으로 반영하고, 대화로만 끝내지 않는다.

---

## 판단 기준

인터뷰 진행이 헷갈리면 아래 질문으로 판단한다.

- 지금 규모에 필요한 질문만 하고 있는가?
- 답이 decision으로 기록되고 있는가?
- 모르는 답을 추측으로 채우지 않았는가?
- 코드 관찰과 사람 확인이 분리되어 있는가?
