# Layer Responsibility

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Feat Agent는 책임 위치를 먼저 정하고 코드를 작성한다.

---

## 출처 정본

[layered-architecture](../../../docs/architecture/layered-architecture.md), [domain-boundary](../../../docs/architecture/domain-boundary.md)

## Presentation

- HTTP 요청/응답을 받고 / 위임 / 돌려주기만 한다.
- Request DTO를 Command / Query로 변환한다.
- 비즈니스 로직과 Repository 접근을 하지 않는다.
- 형식 검증(null / blank / 형식 / 길이)을 Controller에 두지 않는다.
  body는 Request DTO의 Bean Validation(@NotNull/@NotBlank/@Pattern)으로,
  헤더 값은 값 객체 생성 검증이나 ArgumentResolver로 검증한다.
- 인증 / 헤더 검증 실패는 Controller에서 ResponseEntity를 만들지 않고 예외를 던진다.
  에러 봉투(code/errors)는 global 핸들러 한 곳에서만 조립한다.
- nullable 반환값을 신호로 쓰는 제어흐름(`if (x != null) return x`)을 만들지 않는다.

---

## Application

- 유스케이스 흐름을 조율한다.
- 트랜잭션 경계를 가진다.
- Domain, Policy, Validator, Repository를 호출한다.
- 저장소 조회 기반 검증은 Validator에 위임한다.

---

## Domain

- 자기 상태와 비즈니스 규칙을 가진다.
- 상태 변경 행위를 메서드로 표현한다.
- 생성자는 닫고 `create`, `restore`, `of` 같은 정적 팩터리로 생성한다.
- 기술 어노테이션과 외부 시스템 호출을 피한다.

---

## Infrastructure

- DB, 외부 API, 메시징 등 기술 구현을 담당한다.
- Repository 인터페이스를 구현한다.
- 기술 예외는 저장소 의미 예외로 변환한다.

---

## 검증 위치

- HTTP null / blank / 형식 검증: Request DTO
- 자기 상태 기반 도메인 규칙: Domain / Policy
- Repository 조회 기반 검증: Application Validator
- 흐름 조율: Service
