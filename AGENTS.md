# AGENTS.md

이 하네스는 Spring 백엔드 작업자를 위한 base 하네스다.

- 특정 프로젝트의 구현 방식을 고정하지 않는다.
- 변하지 않는 철학, 판단 우선순위, 질문 기준, 책임 경계를 담는다.
- 프로젝트마다 달라질 수 있는 선택은 고정 규칙이 아니라 기본 입장(decision)으로 둔다.
- 프로젝트 / 회사 하네스는 이 하네스를 기반으로 만들고, 자기 decision으로 기본 입장을 덮어쓴다.

이 하네스의 AI 작업자는 아래 순서로 문서를 확인한다.

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/architecture/index.md](./docs/architecture/index.md)
4. [docs/principles/index.md](./docs/principles/index.md)
5. [docs/workflow/index.md](./docs/workflow/index.md)
6. [docs/decisions/README.md](./docs/decisions/README.md)
7. 작업 유형별 필수 문서
8. 작업과 관련된 세부 문서

---

# 작업 시퀀스

요청을 받으면 아래 순서로 진행한다.

1. 요청을 작업 유형으로 분류하고 필수 문서를 확인한다.
2. 상위 전제(저장소 기술 고정 여부, Domain / Persistence Entity 분리 여부, 트랜잭션 성공 기준 등)를 프로젝트의 production code, 문서, accepted decision에서 확인한다.
3. 프로젝트에 decision이 없거나 상위 전제 질문이 반복되면 하네스 인터뷰를 제안한다. 프로젝트 하네스가 이미 있으면 생성 문서를 읽지 않고 프로젝트 하네스를 따른다. → [harness-interview](./docs/workflow/harness-interview.md)
4. 전제가 없거나 충돌하거나 pending 영역이면 구현 전에 질문한다.
5. 큰 요구사항은 기능 목록 / API 명세 / 에러 명세를 먼저 정리한다.
6. Domain → Application Validator → Service → Repository → Controller → Acceptance 순서로 테스트 → 구현 → 리팩터링을 반복한다.
7. 최종 점검을 통과한 뒤 응답한다.

---

# 실행 모드

- 단일 작업자 모드 (기본): 위 작업 시퀀스를 한 작업자가 수행한다.
- 단계 분리 모드: Test → Feat → Review를 스킬(/test-agent, /feat-agent, /review-agent)로 분리 실행한다.
  각 단계는 md 산출물을 남기고, 이전 산출물 없이 다음 단계를 시작하지 않는다.
  각 역할의 하네스는 [sub-agent](./sub-agent), 산출물 규칙은 [agent-handoff-by-artifact](./docs/decisions/accepted/agent-handoff-by-artifact.md)를 따른다.
- 오케스트레이션 모드 (pending): [orchestrator](./orchestrator)가 단계 분리 모드를 자동으로 강제한다. → [orchestration-architecture](./docs/decisions/pending/orchestration-architecture.md)

---

# 판단 우선순위

충돌 시 아래 우선순위로 판단한다.

1. 적용 대상 프로젝트의 production code
2. 적용 대상 프로젝트의 문서와 accepted decision
3. 하네스의 accepted decision (기본 입장)
4. architecture 문서
5. principles 문서
6. workflow 문서

하네스의 기본 입장은 프로젝트에 결정이 없을 때의 출발점이다.
원칙보다 현재 코드 의도가 더 중요할 수 있다.
문서와 코드가 충돌하면 문서 최신성과 코드 의도를 함께 검토한다.
decision의 적용 강도는 [docs/decisions/README.md](./docs/decisions/README.md)를 따른다.

---

# 불변 철학

프로젝트와 무관하게 항상 적용한다.

- 백엔드 작업자는 기획 / 설계 / 리뷰 결정을 대신하지 않는다.
- 확정되지 않은 전제는 임의로 정하지 않고 질문한다.
- 반복되는 고민, 코드와 문서의 충돌, pending 영역 진입을 발견하면 draft decision을 작성해 확인을 요청한다. 결정은 사람 확인 후에만 효력을 가진다.
- 현재 프로젝트의 구조, 의도, 일관성을 우선한다.
- 현재 요구사항 해결에 필요한 범위만 최소 변경한다. 관련 없는 리팩터링은 함께 진행하지 않는다.
- 구조는 먼저 지키되, 추상화와 공통화는 필요가 증명될 때만 도입한다.
- 재사용보다 명시성을 우선한다.
- 현재 코드에 없는 패턴, 라이브러리, 프레임워크를 근거 없이 도입하지 않는다.
- 문서는 현재 코드와 구조를 설명한다. 미래를 위한 추상 설계 문서를 만들지 않는다.
- 절대 push를 하지 않는다.

---

# 기본 입장

고정 규칙이 아니라 이 하네스의 기본 선택이다.
적용 대상 프로젝트에 decision이 있으면 그쪽을 따른다.
선택의 이유와 트레이드오프는 각 링크 문서에 있다.

## 구조

- 트랜잭션 경계는 Service에 둔다. → [transaction-boundary-in-service](./docs/decisions/accepted/transaction-boundary-in-service.md)
- Repository 인터페이스는 Domain에 두고 구현은 Infrastructure에 둔다. → [repository-interface-in-domain](./docs/decisions/accepted/repository-interface-in-domain.md)
- Service는 흐름만 조율하고 판단은 Domain / Policy / Validator에 위임한다. Domain 책임을 Service에 누적하지 않는다. → [service-orchestration-only](./docs/decisions/accepted/service-orchestration-only.md)
- 비즈니스 규칙 판단은 Domain / Policy / Validator에 둔다. Infrastructure는 기술 구현만 담당한다. → [domain-does-not-know-technology](./docs/decisions/accepted/domain-does-not-know-technology.md)
- Controller는 HTTP 입출력만 담당한다. 레이어를 건너뛰는 직접 호출, Controller의 DB 접근과 검증 책임 누적을 하지 않는다. → [layered-architecture](./docs/architecture/layered-architecture.md)
- Domain Entity / Aggregate 생성자는 닫고 정적 팩터리 메서드로 생성한다. → [static-factory-method](./docs/decisions/accepted/static-factory-method.md)
- Service 입력은 Request DTO가 아니라 Command로 분리한다. → [request-command-separation](./docs/decisions/accepted/request-command-separation.md)
- HTTP 입력 필수값(null / blank) 검증은 Request DTO에서 수행하고, Domain / Service에 누적하지 않는다. → [domain-boundary](./docs/architecture/domain-boundary.md)
- 후속 작업 실패는 사용자의 성공 기준으로 트랜잭션 분리를 판단한다. → [follow-up-failure-boundary](./docs/decisions/accepted/follow-up-failure-boundary.md)
- Repository의 기술 예외는 Infrastructure에서 저장소 의미 예외로 변환하고, Application이 필요한 경우 유스케이스 의미로 변환한다. → [infrastructure-exception-translation](./docs/decisions/accepted/infrastructure-exception-translation.md)
- 책임이 불분명한 Helper / Util / Manager 클래스를 만들지 않는다. → [naming](./docs/principles/naming.md), [common-util-package](./docs/decisions/rejected/common-util-package.md)

## 테스트

- 실패하는 테스트를 먼저 확인한 뒤 구현한다. Acceptance Test부터 시작하지 않는다. → [tdd](./docs/workflow/tdd.md)
- 테스트 단위는 기능명이 아니라 production class의 public behavior 기준으로 잡는다. 모든 production class는 직접 테스트하고, Service 테스트나 통합 테스트가 Domain / Policy / Validator 책임 검증을 대체하지 않는다. private method는 직접 테스트하지 않는다. → [public-behavior-based-tdd](./docs/decisions/accepted/public-behavior-based-tdd.md)
- 테스트 더블은 테스트 대상 책임에 따라 Mock / Fake를 선택한다. → [test-double-by-responsibility](./docs/decisions/accepted/test-double-by-responsibility.md)
- 동시성 검증은 실제 Spring Context와 DB를 사용하는 통합 테스트로 한다. → [concurrency-test-boundary](./docs/decisions/accepted/concurrency-test-boundary.md)
- Fake는 test source의 fake 패키지에 분리하고 테스트 클래스 내부에 작성하지 않는다. 세부 위치 기준은 아직 pending이다. → [fake-package-location](./docs/decisions/pending/fake-package-location.md)
- 테스트 패키지 구조는 main 패키지 구조와 일치시킨다. → [testing](./docs/principles/testing.md)
- 테스트 없이 먼저 구현해야 하면 이유를 README.md 또는 docs에 기록한다.

## 작업 방식

- 커밋은 테스트 커밋 → 구현 커밋 순서로 분리하고, 메서드 단위로 commit한다. → [git-convention](./docs/workflow/git-convention.md)
- 코드 리뷰 보강, 테스트 보강, 리팩터링도 public behavior 또는 책임 단위로 커밋한다. 리팩터링 커밋은 행위 변경 없이 하나의 구조 개선만 포함한다. → [git-convention](./docs/workflow/git-convention.md)
- 커밋 메시지 type / scope는 영어, summary와 본문은 한국어로 작성한다. → [git-convention](./docs/workflow/git-convention.md)
- 기계적으로 판정 가능한 규칙은 지침이 아니라 스크립트로 강제한다. → [enforcement-by-script](./docs/decisions/accepted/enforcement-by-script.md)
- 하네스 절차는 스킬(얇은 런처)로 호출하되 정본은 항상 문서다. → [skill-as-thin-launcher](./docs/decisions/accepted/skill-as-thin-launcher.md)

---

# 작업 유형별 필수 문서

## 테스트 / TDD / 커밋

- [docs/workflow/tdd.md](./docs/workflow/tdd.md)
- [docs/principles/testing.md](./docs/principles/testing.md)
- [docs/workflow/git-convention.md](./docs/workflow/git-convention.md)

## 레이어 / 패키지 / 의존성

- [docs/architecture/layered-architecture.md](./docs/architecture/layered-architecture.md)
- [docs/architecture/package-structure.md](./docs/architecture/package-structure.md)

## DB / Repository / 트랜잭션

- [docs/architecture/repository-pattern.md](./docs/architecture/repository-pattern.md)
- [docs/architecture/transactions.md](./docs/architecture/transactions.md)

## 도메인 규칙 / 검증

- [docs/architecture/domain-boundary.md](./docs/architecture/domain-boundary.md)
- [docs/principles/oop.md](./docs/principles/oop.md)
- [docs/principles/lombok.md](./docs/principles/lombok.md)

## 예외 / 에러 응답

- [docs/principles/exceptions.md](./docs/principles/exceptions.md)

## 이름 / 공통화 / 추상화

- [docs/principles/naming.md](./docs/principles/naming.md)
- [docs/decisions/rejected/common-util-package.md](./docs/decisions/rejected/common-util-package.md)

분류가 애매하면 관련 가능성이 있는 문서를 먼저 확인한다.

---

# 최종 점검

최종 응답 전 아래 항목을 확인한다.

- TDD 순서를 지켰는가
- 테스트 / 구현 / 리팩터링 커밋이 책임 단위로 분리되었는가
- 모든 production class는 직접 테스트되었는가
- 직접 테스트하지 않았다면 제외 가능한 이유가 있는가
- Service 테스트에 묻혀 Validator, Policy, Domain 책임 검증을 생략하지 않았는가
- 테스트 패키지 구조와 Fake 위치가 기준에 맞는가
- Domain 생성자와 Request DTO 검증 위치가 기준에 맞는가
- 레이어 책임과 최소 변경 원칙을 지켰는가
- README 또는 docs 업데이트가 필요한가
- 프로젝트와 하네스의 accepted decision과 충돌하지 않는가
