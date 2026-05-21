# AGENTS.md

이 프로젝트의 AI 작업자는 아래 순서로 문서를 확인한다.

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/architecture/index.md](./docs/architecture/index.md)
4. [docs/principles/index.md](./docs/principles/index.md)
5. [docs/workflow/index.md](./docs/workflow/index.md)
6. [docs/decisions/README.md](./docs/decisions/README.md)
7. 작업과 관련된 세부 문서

---

## 최상위 규칙

- 새 코드는 기존 원칙과 현재 코드의 흐름을 먼저 확인하고 작성한다.
- 원칙과 코드가 충돌하면 코드를 억지로 맞추지 말고 문서를 갱신한다.
- 추상적인 일반론보다 현재 프로젝트의 코드와 의도를 우선한다.
- 구조는 먼저 지키되, 추상화는 필요해질 때 도입한다.
- 재사용보다 명시성을 우선한다.
- Spring 백엔드 애플리케이션 기준으로 판단한다.

---

## 금지 사항

- 레이어를 건너뛰는 직접 호출
- 의미 없는 공통화와 추상화
- 도메인 용어 대신 기술 용어 사용
- 책임이 불분명한 Helper / Util / Manager 클래스 추가

---

## 작업 원칙

- 기능은 도메인부터 안쪽에서 바깥 방향으로 구현한다.
- Service는 흐름을 조율하고 판단은 Domain / Policy / Validator에 위임한다.
- 테스트 가능한 구조를 우선한다.
- 새로운 패턴 도입 전 현재 구조와 일관되는지 먼저 검토한다.
- 기능 구현은 실패하는 테스트를 먼저 작성한 뒤 시작한다.
- 커밋은 기대 행위 단위의 테스트 커밋 → 이를 통과시키는 구현 커밋 순서로 분리한다.
- 테스트 없이 먼저 구현해야 하는 예외 상황은 이유를 남기고 관련 문서를 갱신한다.

---

## 문서 우선순위

- accepted decision은 현재 기본 원칙으로 적용한다.
- rejected decision은 기본적으로 도입하지 않는다.
- pending decision은 참고 자료로만 사용하고, 강제 규칙으로 적용하지 않는다.
- pending 영역에서 구현 판단이 필요하면 현재 코드 흐름과 accepted decision을 우선한다.
- 코드와 문서가 충돌하면 코드를 억지로 맞추지 말고, 코드 의도와 문서의 최신성을 함께 검토한다.