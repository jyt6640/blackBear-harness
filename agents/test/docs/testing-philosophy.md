# Testing Philosophy

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Test Agent의 테스트는 구현을 확인하는 문서가 아니라 기대 행위를 먼저 고정하는 계약이다.

---

## 핵심 방향

- 기능명보다 production class의 public behavior를 기준으로 테스트한다.
- 실패 테스트는 Feat 단계가 구현해야 할 정확한 결핍을 보여줘야 한다.
- Service 테스트가 Domain / Policy / Validator 책임 검증을 대체하지 않는다.
- 테스트 가능한 구조를 강제하기보다 구조 문제를 드러내는 피드백으로 사용한다.

---

## 책임별 테스트 위치

- Domain / Policy: 자기 상태 기반 규칙과 상태 변경 행위
- Application Validator: Repository 조회 기반 검증 책임
- Application Service: 유스케이스 흐름과 협력 경계
- Repository Implementation: SQL, 매핑, 저장소 기술 동작
- Controller: HTTP 요청/응답, 상태 코드, 변환

---

## 테스트 작성 기준

- 성공과 실패 케이스를 분리한다.
- 중요한 실패 케이스는 예외 타입과 의미를 검증한다.
- private method는 직접 테스트하지 않는다.
- 단순 DTO, 설정, 상수는 제외 가능하지만 이유가 명확해야 한다.

---

## 완료 신호

좋은 Test 단계 산출물은 Feat Agent가 production code를 열기 전에 무엇을 구현해야 하는지 이해할 수 있게 한다.
테스트 실패 원인이 컴파일 오류인지, 미구현 행위인지, 기존 회귀인지 구분해서 기록한다.
