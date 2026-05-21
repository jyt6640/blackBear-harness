# ARCHITECTURE.md

이 문서는 프로젝트의 백엔드 아키텍처 기준을 정의한다.

세부 규칙은 [docs/architecture/index.md](./docs/architecture/index.md)에서 시작한다.

---

## 핵심 방향

- 도메인을 중심으로 패키지를 구성한다.
- 흐름과 비즈니스 판단을 분리한다.
- 의존성은 안쪽 방향으로만 흐른다.
- 기술보다 비즈니스 규칙을 우선한다.
- Domain은 기술 구현을 알지 못한다.
- 구조는 처음부터 지키되, 추상화는 필요해질 때 도입한다.

---

## 레이어 책임

### Presentation Layer

- HTTP 요청/응답을 처리한다.
- Request/Response DTO를 변환한다.
- 비즈니스 로직을 가지지 않는다.

### Application Layer

- 유스케이스의 흐름을 조율한다.
- Domain, Validator, Repository를 orchestration 한다.
- 정책 판단을 직접 수행하지 않는다.

### Domain Layer

- 핵심 비즈니스 규칙과 상태를 가진다.
- 자기 상태를 스스로 검증한다.
- 가능한 불변성을 유지한다.

### Infrastructure Layer

- DB, 외부 API, 메시징 등 기술 구현을 담당한다.
- Domain/Application의 인터페이스를 구현한다.

---

## 검증 책임

- 자기 상태만으로 검증 가능하면 Domain Layer에 Policy로 둔다.
- 저장소 조회가 필요하면 Application Validator에 둔다.
- Service는 검증을 수행하지 않고 호출만 한다.

---

## 의존 방향

Presentation
↓
Application
↓
Domain

Infrastructure는 Domain/Application의 인터페이스를 구현한다.
