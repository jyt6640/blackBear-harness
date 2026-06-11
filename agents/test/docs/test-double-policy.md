# Test Double Policy

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


테스트 더블은 Mock / Fake 중 하나를 선호로 고르는 것이 아니라 테스트 대상 책임에 따라 선택한다.

---

## 선택 기준

- Service: 흐름과 협력 호출을 검증하므로 Mock이 자연스럽다.
- Validator: 저장소 상태 기반 검증을 하므로 Fake가 자연스러운 경우가 많다.
- Controller: HTTP 레이어 위임을 검증하므로 Mock이 자연스럽다.
- Repository: 실제 DB 또는 슬라이스 테스트로 구현을 검증한다.
- 동시성: Mock / Fake가 아니라 실제 Spring Context와 DB로 검증한다.

---

## Fake 기준

Fake는 테스트 클래스 내부에 두지 않는다.
test source의 fake 패키지에 분리한다.

세부 위치는 아직 pending이므로 프로젝트 decision이 있으면 그쪽을 따른다.
프로젝트 decision이 없다면 작업 카드에 현재 선택과 이유를 기록한다.

---

## Mock 기준

Mock은 구현 세부사항을 고정하기 위한 도구가 아니다.
협력 경계, 예외 발생 시 흐름 중단, 후속 작업 호출 여부처럼 Service의 orchestration 책임을 검증할 때 사용한다.
