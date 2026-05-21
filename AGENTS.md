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
- 새 코드는 기존 원칙, 현재 코드 흐름, accepted decision을 확인한 뒤 작성한다.
- 원칙과 코드가 충돌하면 코드를 억지로 맞추지 말고 코드 의도와 문서 최신성을 함께 검토한다.
- 추상적인 일반론보다 현재 프로젝트의 코드와 의도를 우선한다.
- 구조는 먼저 지키되, 추상화는 필요해질 때만 도입한다.
- 재사용보다 명시성을 우선한다.
- Spring 백엔드 기준으로 판단한다.

---

## 금지 사항
- 레이어를 건너뛰는 직접 호출
- 실패하는 테스트 확인 전 기능 구현
- 기능 단위 테스트 커밋 없이 구현 커밋 진행
- 의미 없는 공통화와 추상화
- 도메인 용어 대신 기술 용어 사용
- 책임이 불분명한 Helper / Util / Manager 클래스 추가

---

## 작업 원칙
- 큰 요구사항은 기능 목록/API 명세/에러 명세를 먼저 정리한 뒤 시작한다.
- 기능은 Domain → Application → Infrastructure → Presentation 순서로 구현한다.
- Controller는 HTTP 요청/응답만 담당하고 DB 접근을 직접 하지 않는다.
- Service는 흐름을 조율하고 판단은 Domain / Policy / Validator에 위임한다.
- Repository 인터페이스는 Domain에 두고 JDBC/SQL 구현은 Infrastructure에 둔다.
- 새 기능은 실패 테스트 작성 → 실패 확인 → 구현 → 통과 확인 → 리팩터링 순서로 진행한다.
- 커밋은 기대 행위 테스트 커밋 → 이를 통과시키는 구현 커밋 순서로 분리한다.
- 테스트 없이 먼저 구현해야 하면, 구현 전에 이유를 README.md 또는 docs에 남긴다.

---

## 문서 우선순위와 최종 점검
- accepted는 기본 원칙, rejected는 도입 금지, pending은 참고 자료로만 적용한다.
- 판단이 필요하면 현재 코드 흐름과 accepted decision을 우선한다.
- 최종 응답 전 TDD 순서, 기능 단위 커밋, 레이어 책임, README/docs 기록 여부를 확인한다.
