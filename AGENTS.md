# AGENTS.md

이 프로젝트의 AI 작업자는 아래 순서로 문서를 확인한다.

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/architecture/index.md](./docs/architecture/index.md)
4. [docs/principles/index.md](./docs/principles/index.md)
5. [docs/workflow/index.md](./docs/workflow/index.md)
6. [docs/decisions/README.md](./docs/decisions/README.md)
7. 작업 유형별 필수 문서
8. 작업과 관련된 세부 문서

---

# 판단 우선순위

충돌 시 아래 우선순위로 판단한다.

1. 현재 동작하는 production code
2. accepted decision
3. architecture 문서
4. principles 문서
5. workflow 문서

원칙보다 현재 코드 의도가 더 중요할 수 있다.
문서와 코드가 충돌하면 문서 최신성과 코드 의도를 함께 검토한다.

---

# 최상위 규칙

- 구현 전 요청을 작업 유형으로 분류하고 필수 문서를 먼저 확인한다.
- 새 코드는 기존 원칙, 현재 코드 흐름, accepted decision을 확인한 뒤 작성한다.
- 현재 프로젝트의 구조와 의도를 우선한다.
- 추상적인 일반론보다 현재 코드베이스의 일관성을 우선한다.
- 구조는 먼저 지키되 추상화는 필요해질 때만 도입한다.
- 재사용보다 명시성을 우선한다.
- 현재 요구사항 해결에 필요한 범위만 수정한다.
- 관련 없는 리팩터링은 함께 진행하지 않는다.
- 기존 구조를 유지한 채 최소 변경으로 해결하는 것을 우선한다.
- 현재 코드에 존재하지 않는 패턴을 임의로 도입하지 않는다.
- 사용 중이지 않은 라이브러리나 프레임워크를 가정하지 않는다.
- 근거 없는 디자인 패턴과 추상화를 추가하지 않는다.
- Spring 백엔드 기준으로 판단한다.

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

## 예외 / 에러 응답

- [docs/principles/exceptions.md](./docs/principles/exceptions.md)

## 이름 / 공통화 / 추상화

- [docs/principles/naming.md](./docs/principles/naming.md)
- [docs/decisions/rejected/common-util-package.md](./docs/decisions/rejected/common-util-package.md)

분류가 애매하면 관련 가능성이 있는 문서를 먼저 확인한다.

---

# 금지 사항

- 레이어를 건너뛰는 직접 호출
- 실패 테스트 확인 전 기능 구현
- 테스트 없이 구현 커밋 먼저 진행
- Acceptance Test부터 시작하는 구현
- Service 테스트로 Domain / Policy / Validator 책임 검증을 대체하는 방식
- production class를 직접 테스트하지 않고 통합 테스트로만 덮는 방식
- 의미 없는 공통화와 추상화
- 도메인 용어 대신 기술 용어 사용
- 책임이 불분명한 Helper / Util / Manager 클래스 추가
- 여러 역할을 동시에 가지는 Service 작성
- Domain 책임을 Service에 누적
- 검증 책임을 Controller에 누적
- Infrastructure에서 비즈니스 규칙 판단

---

# 작업 원칙

- 큰 요구사항은 기능 목록 / API 명세 / 에러 명세를 먼저 정리한 뒤 시작한다.
- 새 기능은 Domain → Application Validator → Service → Repository → Controller → Acceptance 순서로 테스트와 구현을 진행한다.
- Controller는 HTTP 요청/응답만 담당한다.
- Controller는 DB 접근을 직접 하지 않는다.
- Service는 흐름만 조율하고 판단은 Domain / Policy / Validator에 위임한다.
- Repository 인터페이스는 Domain에 둔다.
- JDBC / SQL 구현은 Infrastructure에 둔다.
- 새 기능은 실패 테스트 작성 → 실패 확인 → 구현 → 통과 확인 → 리팩터링 순서로 진행한다.
- 커밋은 테스트 커밋 → 구현 커밋 순서로 분리한다.
- 테스트 없이 먼저 구현해야 하면 이유를 README.md 또는 docs에 기록한다.
- Acceptance Test는 시작점이 아니라 최종 검증이다.
- Service 테스트가 Domain / Policy / Validator 책임 검증을 대체해서는 안 된다.
- 테스트 단위는 기능명이 아니라 production class의 public behavior 기준으로 잡는다.
- private method를 직접 테스트하지 않는다. private method로 숨겨진 책임은 public behavior 테스트로 드러나야 한다.
- 테스트 패키지 구조는 main 패키지 구조와 일치시킨다.
- 커밋 메시지 type과 scope는 영어로 작성한다.
- 커밋 메시지 summary와 본문은 한국어로 작성한다.

---

# 문서 원칙

- accepted는 기본 원칙으로 따른다.
- rejected는 도입 금지로 판단한다.
- pending은 참고 자료로만 사용한다.
- 반복적으로 발생하는 의사결정만 문서화한다.
- 문서는 현재 코드와 구조를 설명해야 한다.
- 미래를 위한 추상 설계 문서를 만들지 않는다.

---

# 최종 점검

최종 응답 전 아래 항목을 확인한다.

- TDD 순서를 지켰는가
- 테스트 커밋과 구현 커밋이 분리되었는가
- 모든 production class는 직접 테스트되었는가
- 직접 테스트하지 않았다면 단순 DTO / 설정 / 부트스트랩 / 상수처럼 제외 가능한 이유가 있는가
- Service 테스트에 묻혀 Validator, Policy, Domain 책임 검증을 생략하지 않았는가
- 테스트 패키지 구조가 main 패키지 구조와 일치하는가
- 레이어 책임이 유지되었는가
- 불필요한 추상화가 추가되지 않았는가
- 최소 변경 원칙을 지켰는가
- README 또는 docs 업데이트가 필요한가
- accepted decision과 충돌하지 않는가