# AGENTS.md

이 하네스는 Spring 백엔드 기능 개발을 Test -> Feat -> Refactor / Review 단계로
오케스트레이션하기 위한 base 하네스다.

- 특정 프로젝트의 구현 방식을 고정하지 않는다.
- 변하지 않는 철학, 판단 우선순위, 질문 기준, 책임 경계를 담는다.
- 프로젝트마다 달라질 수 있는 선택은 고정 규칙이 아니라 기본 입장(decision)으로 둔다.
- 프로젝트 / 회사 하네스는 이 하네스를 기반으로 만들고, 자기 decision으로 기본 입장을 덮어쓴다.
- AGENTS.md는 직접 구현자 지침이 아니라 오케스트레이터의 진입점이다.

오케스트레이터는 아래 순서로 문서를 확인한다.

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/architecture/index.md](./docs/architecture/index.md)
4. [docs/principles/index.md](./docs/principles/index.md)
5. [docs/workflow/index.md](./docs/workflow/index.md)
6. [docs/decisions/README.md](./docs/decisions/README.md)
7. 작업 유형별 필수 문서
8. 작업과 관련된 세부 문서
9. 역할별 [agents](./agents) 하네스와 docs

---

# 오케스트레이터 책임

오케스트레이터는 직접 구현하지 않고 작업을 분류, 컴파일, 배정, 검증 흐름으로 넘긴다.

- 요청을 작업 유형으로 분류한다.
- production code, 프로젝트 문서, accepted decision을 먼저 확인한다.
- pending 영역이나 충돌을 발견하면 구현 전에 질문하거나 draft decision을 만든다.
- 기능 개발 요청은 작업 카드로 쪼개고 단계 산출물 체인을 강제한다.
- 역할 에이전트가 읽을 지침을 `next-step/work/<작업명>/00-task-card.md`에 컴파일한다. 이번 작업에 적용되는 상위 docs 규칙을 출처와 함께 카드의 `이번 작업 컴파일 규칙`에 적는다. → [task-card-compiles-rules](./docs/decisions/accepted/task-card-compiles-rules.md)
- 이전 단계 산출물이 없으면 다음 단계를 시작하지 않는다.
- 승인된 결과만 통합하고 최종 보고한다.

오케스트레이터가 작성하는 작업 카드는 역할 에이전트의 실행 입력이다.
역할 에이전트는 전체 docs를 다시 해석하기보다 자기 AGENTS.md와 작업 카드를 따른다.

---

# 작업 시퀀스

요청을 받으면 아래 순서로 진행한다.

1. 요청을 작업 유형으로 분류하고 필수 문서를 확인한다.
2. 상위 전제(저장소 기술 고정 여부, Domain / Persistence Entity 분리 여부, 트랜잭션 성공 기준 등)를 프로젝트의 production code, 문서, accepted decision에서 확인한다.
3. 프로젝트에 decision이 없거나 상위 전제 질문이 반복되면 하네스 인터뷰를 제안한다. 프로젝트 하네스가 이미 있으면 생성 문서를 읽지 않고 프로젝트 하네스를 따른다. → [harness-interview](./docs/workflow/harness-interview.md)
4. 전제가 없거나 충돌하거나 pending 영역이면 구현 전에 질문한다.
5. 카드가 2장 이상 필요한 요청이면 `next-step/work/backlog.md`를 먼저 작성한다. 카드 목록과 순서는 대화 기억이 아니라 백로그에 둔다.
6. 기능 개발이면 `next-step/work/<작업명>/00-task-card.md`를 작성한다. 카드는 백로그 순번대로 한 장씩 컴파일하고 미리 만들지 않는다.
7. 사용자 릴레이 모드에서는 카드 작성이 끝나면 멈추고 사용자에게 /test-agent 실행을 요청한다.
8. 자동 로컬 에이전트 모드에서는 카드 작성 후 `scripts/local-agent/run-pipeline.sh`에 넘긴다.
9. 단계 릴레이: Test → Feat → Refactor → Review. 사용자 릴레이 모드에서는 각 단계를 사용자의 스킬 호출로 시작한다. 자동 모드에서는 오케스트레이터가 역할별 로컬 에이전트를 직렬 호출하되 산출물 경계와 게이트를 동일하게 지킨다.
10. Review가 반려하면 사유에 따라 Feat, Refactor, Test로 되돌린다. 자동 모드는 보고서의 `재실행 단계`를 읽어 제한 횟수 안에서 재실행하고, 한도를 넘으면 오케스트레이터에게 올린다.
11. 승인 후 `04-summary.md`를 작성하고 백로그 상태를 갱신한다. 최종 점검을 통과한 뒤 영구화할 내용만 docs / decision / 커밋 메시지 / PR 설명으로 승격한다.
12. 카드 완료 후 `next-step/work/<작업명>`을 삭제하지 않고 `next-step/history/<작업명>/`으로 이동해 보존한다.
13. 백로그의 모든 카드가 끝나면 `next-step/work/backlog.md`를 `next-step/history/<백로그명>-backlog.md`로 이동해 보존한다.
14. 백로그 완료 시 또는 카드 5장마다 `scripts/loop/aggregate-scores.sh`로 철학 점수를 집계하고, 낮은 항목은 /loop-improve로 프롬프트 개선을 제안한다. → [loop-engineering](./docs/workflow/loop-engineering.md)

---

# 산출물 체인

기능 개발은 아래 산출물 체인을 기본으로 한다.

    next-step/work/
    ├── backlog.md                  (카드 2장 이상일 때: 카드 목록·순서·진행 상태)
    └── <작업명>/
    ├── 00-task-card.md
    ├── 01-test-report.md
    ├── 02-implementation-report.md
    ├── 02-refactor-report.md
    ├── 03-review-report.md
    ├── 04-summary.md
    └── 05-scorecard.md

각 산출물의 역할:

- `00-task-card.md`: 오케스트레이터가 docs와 decisions를 현재 작업용 지침으로 컴파일한 작업 정의
- `01-test-report.md`: Test Agent가 작성한 실패 테스트와 실패 확인 결과
- `02-implementation-report.md`: Feat Agent가 실패 테스트를 통과시킨 구현 결과
- `02-refactor-report.md`: Refactor Agent의 행위 보존 확인이 포함된 구조 개선 결과
- `03-review-report.md`: Review Agent의 승인 / 반려 판정
- `05-scorecard.md`: Review Agent의 철학 점수표 (항목 정본은 next-step/templates/05-scorecard.md)
- `04-summary.md`: 오케스트레이터의 통합 보고와 영구화 여부 판단

단계 게이트:

- `00-task-card.md`가 없으면 Test를 시작하지 않는다.
- `01-test-report.md`가 없으면 Feat를 시작하지 않는다.
- `02-implementation-report.md`가 없으면 Review를 시작하지 않는다.
- feature 카드는 `02-refactor-report.md` 없이 Review를 시작하지 않는다. 개선할 것이 없으면 보고서에 "개선 사항 없음"과 행위 보존 확인을 기록한다.
- refactor 전용 카드는 행위 변경이 없으므로 `01-test-report.md` 없이 진행할 수 있고, `02-refactor-report.md`를 `02-implementation-report.md`와 동등하게 인정한다.
- Review가 반려하면 Feat 또는 Test로 되돌린다.
- 승인 없이 기능 완료로 보고하지 않는다.

`next-step/work/`는 모델 교체와 단계 분리를 위한 단기 작업 메모리다.
작업 중에는 커밋하지 않는다. Review 승인과 `04-summary.md` 작성이 끝나면
`next-step/history/`로 이동하고 완료 기록으로 커밋한다.
산출물 형식의 정본은 [next-step/templates](./next-step/templates/00-task-card.md)에 둔다.

---

# 실행 모드

- 사용자 릴레이 모드 (기본): 각 단계는 사용자의 스킬 호출로만 시작한다.
  /orchestrate → /test-agent → /feat-agent → /refactor-agent → /review-agent.
  한 모델이 여러 역할을 수행할 수 있지만, 단계가 끝나면 반드시 멈추고 사용자에게 다음 스킬 실행을 요청한다.
  산출물 경계와 정지 규칙은 생략할 수 없다.
- Refactor 전용 카드: 행위 변경 없는 구조 개선만 별도 카드로 다룬다. [agents/refactor](./agents/refactor)
- 자동 로컬 에이전트 모드: 오케스트레이터가 작업 카드까지 컴파일한 뒤 역할별 로컬 LLM을 직렬 호출한다. 역할 에이전트는 자기 하네스, 카드에 지정된 상위 문서, 이전 단계 산출물만 읽는다. → [local-llm-agent-orchestration](./docs/decisions/accepted/local-llm-agent-orchestration.md)

스킬은 역할 실행을 시작하는 얇은 런처다.
정본은 루트 문서, 역할별 AGENTS.md, 역할별 docs, decisions에 있다.

---

# 역할별 하네스

역할별 AGENTS.md는 해당 역할의 책임, 입력, 출력, 금지, 완료 기준을 정의한다.

- Test Agent: [agents/test/AGENTS.md](./agents/test/AGENTS.md)
- Feat Agent: [agents/feat/AGENTS.md](./agents/feat/AGENTS.md)
- Refactor Agent: [agents/refactor/AGENTS.md](./agents/refactor/AGENTS.md)
- Review Agent: [agents/review/AGENTS.md](./agents/review/AGENTS.md)

역할별 docs는 실행 관점의 철학과 기준을 담는다.
공통 판단 정본은 여전히 `docs/architecture`, `docs/principles`, `docs/decisions`다.

---

# 판단 우선순위

충돌 시 아래 우선순위로 판단한다.

1. 적용 대상 프로젝트의 production code
2. 적용 대상 프로젝트의 문서와 accepted decision
3. 하네스의 accepted decision (기본 입장)
4. architecture 문서
5. principles 문서
6. workflow 문서
7. 역할별 docs
8. 작업 카드

하네스의 기본 입장은 프로젝트에 결정이 없을 때의 출발점이다.
원칙보다 현재 코드 의도가 더 중요할 수 있다.
문서와 코드가 충돌하면 문서 최신성과 코드 의도를 함께 검토한다.
decision의 적용 강도는 [docs/decisions/README.md](./docs/decisions/README.md)를 따른다.

작업 카드는 정본을 대체하지 않는다.
작업 카드는 오케스트레이터가 현재 작업에 필요한 지침을 실행 가능하게 묶은 단기 입력이다.

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
- 접근 권한 판단(소유권·가시성·관계)은 Policy 책임이다. Service에 분기로 두지 않고, 도메인 상태 + 저장소 조회 조합이면 도메인이 규칙을 갖고 외부 사실을 인자로 받는다. → [authorization-policy-placement](./docs/decisions/accepted/authorization-policy-placement.md)

## 테스트

- 실패하는 테스트를 먼저 확인한 뒤 구현한다. Acceptance Test부터 시작하지 않는다. → [tdd](./docs/workflow/tdd.md)
- 테스트 단위는 기능명이 아니라 production class의 public behavior 기준으로 잡는다. 모든 production class는 직접 테스트하고, Service 테스트나 통합 테스트가 Domain / Policy / Validator 책임 검증을 대체하지 않는다. private method는 직접 테스트하지 않는다. → [public-behavior-based-tdd](./docs/decisions/accepted/public-behavior-based-tdd.md)
- 테스트 더블은 테스트 대상 책임에 따라 Mock / Fake를 선택한다. → [test-double-by-responsibility](./docs/decisions/accepted/test-double-by-responsibility.md)
- 동시성 검증은 실제 Spring Context와 DB를 사용하는 통합 테스트로 한다. → [concurrency-test-boundary](./docs/decisions/accepted/concurrency-test-boundary.md)
- Fake는 test source의 fake 패키지에 분리하고 테스트 클래스 내부에 작성하지 않는다. 세부 위치 기준은 아직 pending이다. → [fake-package-location](./docs/decisions/pending/fake-package-location.md)
- 테스트 패키지 구조는 main 패키지 구조와 일치시킨다. → [testing](./docs/principles/testing.md)
- 테스트 없이 먼저 구현해야 하면 이유를 README.md 또는 docs에 기록한다.

## 작업 방식

- 기능 개발은 테스트 산출물 없이 구현하지 않는다. → [agent-handoff-by-artifact](./docs/decisions/accepted/agent-handoff-by-artifact.md)
- 자동 로컬 에이전트는 작업 카드에 컴파일된 상위 문서만 읽고, provider와 모델 설정은 저장소 밖 Codex profile에서 받는다. → [local-llm-agent-orchestration](./docs/decisions/accepted/local-llm-agent-orchestration.md)
- 커밋은 테스트 커밋 → 구현 커밋 순서로 분리하고, public behavior 또는 책임 단위로 commit한다. → [git-convention](./docs/workflow/git-convention.md)
- 코드 리뷰 보강, 테스트 보강, 리팩터링도 public behavior 또는 책임 단위로 커밋한다. 리팩터링 커밋은 행위 변경 없이 하나의 구조 개선만 포함한다. → [git-convention](./docs/workflow/git-convention.md)
- 커밋 메시지 type / scope는 영어, summary와 본문은 한국어로 작성한다. → [git-convention](./docs/workflow/git-convention.md)
- 기계적으로 판정 가능한 규칙은 지침이 아니라 스크립트로 강제한다. → [enforcement-by-script](./docs/decisions/accepted/enforcement-by-script.md)
- 근간 철학(architecture / principles) 중 AST로 판정 가능한 구조 규칙(Tier 1)은 ArchUnit 테스트로 강제하고 verify.sh green-bar에 포함한다. 의미 규칙은 Review / 점수표가, 스타일은 린터가 맡는다. → [architecture-rules-as-archunit](./docs/decisions/accepted/architecture-rules-as-archunit.md)
- 하네스 절차는 스킬(얇은 런처)로 호출하되 정본은 항상 문서다. → [skill-as-thin-launcher](./docs/decisions/accepted/skill-as-thin-launcher.md)

---

# 작업 유형별 필수 문서

## 기능 개발 / 오케스트레이션

- [docs/workflow/how-to-add-new-feature.md](./docs/workflow/how-to-add-new-feature.md)
- [docs/workflow/local-agent-orchestration.md](./docs/workflow/local-agent-orchestration.md) — 자동 로컬 에이전트 모드
- [docs/decisions/accepted/agent-handoff-by-artifact.md](./docs/decisions/accepted/agent-handoff-by-artifact.md)
- [agents/test/AGENTS.md](./agents/test/AGENTS.md)
- [agents/feat/AGENTS.md](./agents/feat/AGENTS.md)
- [agents/review/AGENTS.md](./agents/review/AGENTS.md)

## 테스트 / TDD / 커밋

- [docs/workflow/tdd.md](./docs/workflow/tdd.md)
- [docs/principles/testing.md](./docs/principles/testing.md)
- [docs/workflow/git-convention.md](./docs/workflow/git-convention.md)
- [agents/test/docs/testing-philosophy.md](./agents/test/docs/testing-philosophy.md)
- [agents/test/docs/tdd-workflow.md](./agents/test/docs/tdd-workflow.md)

## 레이어 / 패키지 / 의존성

- [docs/architecture/layered-architecture.md](./docs/architecture/layered-architecture.md)
- [docs/architecture/package-structure.md](./docs/architecture/package-structure.md)
- [agents/feat/docs/layer-responsibility.md](./agents/feat/docs/layer-responsibility.md)

## DB / Repository / 트랜잭션

- [docs/architecture/repository-pattern.md](./docs/architecture/repository-pattern.md)
- [docs/architecture/transactions.md](./docs/architecture/transactions.md)

## 도메인 규칙 / 검증

- [docs/architecture/domain-boundary.md](./docs/architecture/domain-boundary.md)
- [docs/principles/oop.md](./docs/principles/oop.md)
- [docs/principles/lombok.md](./docs/principles/lombok.md)

## 리팩터링

- [docs/workflow/refactoring.md](./docs/workflow/refactoring.md)
- [agents/refactor/AGENTS.md](./agents/refactor/AGENTS.md)
- [agents/refactor/docs/refactoring-philosophy.md](./agents/refactor/docs/refactoring-philosophy.md)

## 리뷰

- [docs/workflow/code-review.md](./docs/workflow/code-review.md)
- [agents/review/AGENTS.md](./agents/review/AGENTS.md)
- [agents/review/docs/review-philosophy.md](./agents/review/docs/review-philosophy.md)

## 예외 / 에러 응답

- [docs/principles/exceptions.md](./docs/principles/exceptions.md)

## 이름 / 공통화 / 추상화

- [docs/principles/naming.md](./docs/principles/naming.md)
- [docs/decisions/rejected/common-util-package.md](./docs/decisions/rejected/common-util-package.md)

분류가 애매하면 관련 가능성이 있는 문서를 먼저 확인한다.

---

# 최종 점검

최종 응답 전 아래 항목을 확인한다.

- 작업 카드가 필요한 작업인데 `00-task-card.md` 없이 진행하지 않았는가
- 단계 전환을 사용자 스킬 호출 없이 연속 실행하지 않았는가
- Feat 단계가 `01-test-report.md` 없이 시작되지 않았는가
- Review 단계가 Refactor 단계 보고서 없이 시작되지 않았는가 (feature 카드)
- Review 단계가 `02-implementation-report.md` 없이 시작되지 않았는가
- TDD 순서를 지켰는가
- 테스트 / 구현 / 리팩터링 커밋이 책임 단위로 분리되었는가
- 모든 production class는 직접 테스트되었는가
- 직접 테스트하지 않았다면 제외 가능한 이유가 있는가
- Service 테스트에 묻혀 Validator, Policy, Domain 책임 검증을 생략하지 않았는가
- 테스트 패키지 구조와 Fake 위치가 기준에 맞는가
- Domain 생성자와 Request DTO 검증 위치가 기준에 맞는가
- 레이어 책임과 최소 변경 원칙을 지켰는가
- Refactor가 public behavior 변경과 섞이지 않았는가
- README 또는 docs 업데이트가 필요한가
- 프로젝트와 하네스의 accepted decision과 충돌하지 않는가
- 완료 후 `next-step/work/<작업명>`에서 영구화할 지식을 승격하고 전체 산출물을 `next-step/history/<작업명>/`으로 이동했는가
