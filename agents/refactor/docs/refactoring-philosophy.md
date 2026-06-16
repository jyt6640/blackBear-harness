# Refactor — 실행 체크리스트

> 역할 실행 문서다. 철학 본문은 여기 쓰지 않는다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.

## 출처 정본

철학은 정본에서 읽는다(여기 재서술하지 않는다):
[refactoring](../../../docs/workflow/refactoring.md), [method-design](../../../docs/principles/method-design.md), [naming](../../../docs/principles/naming.md)

---

## 리팩터링 신호 (이 중 하나면 후보)

- 이름이 구현을 설명하고 의도를 드러내지 못한다.
- Service에 정책 판단이 누적된다.
- getter로 상태를 꺼내 외부에서 판단한다.
- util/helper/manager가 커진다.
- 메서드 이름에 and가 들어간다.
- 조건문이 정책을 숨긴다.

## 실행 기준 (체크리스트)

- [ ] 행위를 바꾸지 않았는가 (전후 동일 테스트 green)
- [ ] 하나의 구조 개선 단위로 작게 끝냈는가
- [ ] 행위 변경이 필요해지면 멈추고 feat/fix 카드로 분리했는가
