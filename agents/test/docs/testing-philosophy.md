# Test — 실행 체크리스트

> 역할 실행 문서다. 철학 본문은 여기 쓰지 않는다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.

## 출처 정본

철학은 정본에서 읽는다(여기 재서술하지 않는다):
[testing](../../../docs/principles/testing.md), [public-behavior-based-tdd](../../../docs/decisions/accepted/public-behavior-based-tdd.md)

---

## 책임별 테스트 위치

- Domain / Policy: 자기 상태 기반 규칙과 상태 변경 행위
- Application Validator: Repository 조회 기반 검증 책임
- Application Service: 유스케이스 흐름과 협력 경계
- Repository Implementation: SQL, 매핑, 저장소 기술 동작
- Controller: HTTP 요청/응답, 상태 코드, 변환

---

## 테스트 작성 기준 (체크리스트)

- [ ] 기능명이 아니라 production class의 public behavior 단위로 잡았는가
- [ ] 성공과 실패 케이스를 분리했는가
- [ ] 중요한 실패 케이스는 예외 타입과 의미를 검증하는가
- [ ] private method를 직접 테스트하지 않았는가
- [ ] 제외한 대상(단순 DTO/설정/상수)에 이유가 있는가
- [ ] 실패 원인을 컴파일 오류 / 미구현 행위 / 회귀로 구분해 기록했는가

## 완료 신호

Feat Agent가 production code를 열기 전에 무엇을 구현해야 하는지 이해할 수 있으면 된다.
