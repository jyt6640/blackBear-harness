# Test Agent Workflow

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Test Agent의 workflow는 작업 카드의 기대 행위를 production class의 public behavior 테스트로 고정하는 절차다.
구현을 예측해서 코드를 쓰는 단계가 아니라, 다음 Feat 단계가 따라야 할 행위 계약을 만드는 단계다.

---

## 시작 순서

1. `agents/test/scripts/enforce-workflow.sh <작업명>`을 실행한다.
2. `next-step/work/<작업명>/00-task-card.md`를 읽고 대상 행위, 대상 레이어, 금지 사항, 완료 기준을 확인한다.
3. `docs/workflow/tdd.md`, `docs/principles/testing.md`, 역할 docs를 확인한다.
4. 작업 카드의 행위를 production class public behavior 단위로 쪼갠다.
5. 테스트 작성 순서를 정하고, 하나의 행위씩 실패 테스트를 작성한다.

작업 카드가 모호하거나 테스트 대상 class가 정해지지 않으면 테스트를 추측해서 만들지 않는다.
오케스트레이터에게 작업 카드 보강을 요청한다.

---

## 테스트 작성 순서

기본 순서는 안쪽에서 바깥쪽으로 진행한다.

1. Domain public behavior test
2. Application Validator public validation test
3. Service orchestration test
4. Repository SQL / mapping test
5. Controller HTTP / DTO contract test
6. Acceptance scenario test

Acceptance Test는 시작점이 아니라 마지막 검증이다.
Service 테스트가 Domain, Policy, Validator 책임 검증을 대신하지 않는다.

---

## 작성 방식

- 테스트 단위는 기능명이 아니라 production class의 public behavior다.
- 모든 production class는 직접 테스트하는 것을 기본으로 한다.
- private method는 직접 테스트하지 않는다.
- 테스트 패키지는 main 패키지 구조와 일치시킨다.
- Fake는 test source의 fake 패키지에 둔다.
- Service 테스트에서는 Spring Context를 띄우지 않는다.
- Controller 테스트는 MVC 레이어만 로드하고 Service는 Mock으로 대체한다.
- Repository 테스트는 실제 DB 또는 슬라이스 테스트로 SQL과 매핑을 검증한다.
- 동시성 테스트는 실제 Spring Context와 DB를 사용하는 통합 테스트로 작성한다.

---

## 테스트 코드 컨벤션

모든 테스트는 아래 형식을 기본으로 한다.

```java
@Test
@DisplayName("회원을 생성한다")
void create_success() {
    // given

    // when

    // then
}
```

- 모든 테스트에 `@Test`와 `@DisplayName`을 작성한다.
- `@DisplayName`은 한국어로 작성한다.
- 메서드명은 `method_success`, `method_fail_with_이유`, `method_success_when_조건` 형식을 따른다.
- 본문은 given / when / then 주석을 기본으로 사용한다.
- 예외 검증처럼 when과 then이 합쳐지면 `// when & then`을 사용한다.

---

## 실패 확인

테스트 작성 후 실제 실패를 확인한다.

- 실패 원인은 production 구현 부재 또는 기존 행위 불일치여야 한다.
- 문법 오류, 테스트 설정 오류, 잘못된 fixture 때문에 실패하면 Test 단계 안에서 테스트를 고친다.
- 실패가 작업 카드와 충돌하면 테스트를 억지로 맞추지 않고 오케스트레이터에게 보고한다.

---

## 보고서 작성

`next-step/work/<작업명>/01-test-report.md`에 아래를 기록한다.

- 커버한 public behavior
- 작성한 테스트 파일과 테스트 메서드
- 선택한 Mock / Fake와 이유
- 실행한 명령
- 실패 확인 결과
- Feat Agent가 지켜야 할 제약

보고서는 Feat Agent의 입력이다.
테스트 코드만 남기고 보고서를 생략하면 Feat 단계로 넘길 수 없다.
