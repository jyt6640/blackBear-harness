# TDD Workflow

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Test Agent는 Red 단계만 담당한다.
Green은 Feat Agent의 책임이다.

---

## 출처 정본

[tdd](../../../docs/workflow/tdd.md), [public-behavior-based-tdd](../../../docs/decisions/accepted/public-behavior-based-tdd.md)

## 흐름

1. `00-task-card.md`에서 대상 행위와 완료 기준을 확인한다.
2. 관련 production class와 기존 테스트 구조를 읽는다.
3. 가장 안쪽 책임부터 실패 테스트를 작성한다.
4. 테스트를 실행해 실패를 확인한다.
5. 실패 로그 요약과 의도를 `01-test-report.md`에 기록한다.

---

## 테스트 순서

기본 순서는 아래와 같다.

1. Domain / Policy
2. Application Validator
3. Application Service
4. Repository
5. Controller
6. Acceptance

Acceptance Test는 전체 시나리오 확인용이다.
핵심 규칙을 Acceptance Test에만 묻지 않는다.

---

## 실패 확인

실패 확인은 단순히 테스트를 작성했다는 뜻이 아니다.
실제 테스트 명령을 실행하고, 기대한 이유로 실패했는지 확인해야 한다.

실패가 작업 카드의 행위 결핍이 아니라 환경 문제, 기존 회귀, 모순된 요구 때문이면 Feat로 넘기지 않는다.
