# Authorization Policy Placement

## 상태

accepted

---

## 문제 상황

"이 사용자가 이 리소스를 볼/바꿀 권한이 있는가"(소유권·가시성·관계 기반 접근 판단)의
책임 위치가 docs에 없었다. docs가 정의한 검증은 세 종류뿐이었다.

- 형식 검증 → Request DTO
- 도메인 상태 검증 → Domain / Policy
- 저장소 조회 검증 → Application Validator

접근 권한 판단은 이 셋 어디에도 매핑되지 않았고, [security-auth-pattern](../pending/security-auth-pattern.md)은
인증 *메커니즘*(JWT/Security/Interceptor)만 다루지 *접근 권한을 어느 객체가 판단하나*는
다루지 않는다. 갈 곳이 없자 권한 판단이 Service의 if 체인으로 흘러들어갔다.

실제 관찰된 코드(요약):

    if (schedule.getMemberId().equals(memberId)) return;
    if (schedule.getVisibility() == Visibility.PUBLIC) return;
    if (schedule.getParticipantIds().contains(memberId)) return;
    if (schedule.getVisibility() == Visibility.FRIENDS_ONLY) {
        boolean isFriend = friendshipRepository.existsBy... ;
        if (isFriend) return;
    }
    throw new ForbiddenScheduleAccessException();

이 규칙은 도메인 상태(visibility/owner/participants)와 저장소 조회(friendship)를
*둘 다* 필요로 한다. docs의 Policy vs Validator 경계("자기 상태만으로 판단 가능한가?")
이분법으로는 위치가 안 나온다.

선택지:

- A. 도메인이 규칙을 갖고, 외부 사실(친구 여부)은 인자로 받는다.
- B. Application Policy / Validator가 규칙 전체를 갖고, 도메인은 getter를 노출한다.

---

## 선택한 방향

권한 판단은 **Policy 책임**이다. Service에 두지 않는다.

규칙이 도메인 상태와 저장소 조회를 함께 필요로 하면 **A안**으로 푼다.

- 규칙 본체는 도메인이 소유한다. Domain 객체의 행위 또는 도메인 Policy로 표현한다.
  - 예: `schedule.validateViewableBy(viewerId, isFriend)` 또는
    `ScheduleAccessPolicy.validateViewable(schedule, viewerId, isFriend)`
- 저장소 조회가 필요한 외부 사실은 Application이 Reference 포트로 조회해 **인자로 주입**한다.
  - 예: `boolean isFriend = friendReference.areFriends(schedule.ownerId(), viewerId)`
  - → [domain-reference-adapter](./domain-reference-adapter.md)
- Application Service / Validator는 *조회 + 도메인 행위 호출*만 조율한다. 권한 분기를 직접 갖지 않는다.

권한 실패는 도메인/정책이 의미 예외(ErrorCode)로 던진다. → [exception-hierarchy](./exception-hierarchy.md)

---

## 선택 이유

### 도메인 순수성을 지키면서 규칙을 도메인에 둔다

친구 여부를 boolean으로 주입하므로 도메인은 Repository를 모른다.
[architecture-rules-as-archunit](./architecture-rules-as-archunit.md)의 Domain 순수성
규칙과 충돌하지 않는다.

### 반(反)빈혈 도메인

B안은 분기를 Service에서 Validator로 옮길 뿐 도메인은 여전히 getter 덩어리로 수동적이다.
A안은 가시성 규칙의 주인을 도메인으로 둔다. → [anemic-domain-model](../rejected/anemic-domain-model.md), Tell-Don't-Ask

### Service가 도메인 언어로 읽힌다

권한 분기가 도메인 행위로 흡수되면 Service 본문이 "조회하고 → 권한 확인하고 → 처리"로
읽힌다. [service-orchestration-only](./service-orchestration-only.md)의 의도가 코드에 드러난다.

---

## 트레이드오프

### 외부 사실 주입의 사전 조회

도메인이 repo를 모르므로, 분기에서야 필요한 외부 사실도 Application이 미리 조회해
주입해야 할 수 있다(불필요한 조회 가능). 조회 비용이 크면 Reference 포트를 지연 평가
가능한 형태(함수/공급자)로 주입하는 변형을 둘 수 있다.

### 인증 메커니즘은 별개

이 결정은 *접근 권한 판단의 위치*만 정한다. 인증 수단(JWT/Security 등)은
여전히 [security-auth-pattern](../pending/security-auth-pattern.md) pending이다.

---

## 현재 판단

권한 판단은 Policy 책임으로 고정한다. 도메인 상태 + 저장소 조회 조합 정책은 A안
(도메인 규칙 + Reference 포트로 외부 사실 주입)을 기본으로 한다.
이 결정은 docs/architecture/domain-boundary.md의 검증 책임 표와 layered-architecture.md의
Application 책임에 반영한다.

---

## 재검토 신호

- 권한 종류가 늘어 도메인 행위 인자가 비대해진다 (전용 AccessContext 객체 검토).
- 외부 사실 사전 조회 비용이 반복적으로 문제가 된다 (지연 주입 / 캐시 검토).
- 권한 규칙이 여러 도메인에 걸쳐 한 도메인에 두기 어색해진다 (전용 권한 도메인 검토).
