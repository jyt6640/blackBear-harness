# Refactoring Philosophy

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Refactor Agent는 행위를 바꾸지 않고 의도와 구조를 더 선명하게 만든다.

---

## 출처 정본

[refactoring](../../../docs/workflow/refactoring.md), [method-design](../../../docs/principles/method-design.md), [naming](../../../docs/principles/naming.md)

## 핵심 방향

- 이름을 먼저 본다.
- 책임이 섞인 곳을 분리한다.
- 구조보다 의도를 명확하게 만든다.
- 추상화는 필요가 증명될 때만 도입한다.
- 리팩터링은 하나의 구조 개선 단위로 작게 끝낸다.

---

## 리팩터링 신호

- 이름이 구현을 설명하고 의도를 드러내지 못한다.
- Service에 정책 판단이 누적된다.
- getter로 상태를 꺼내 외부에서 판단한다.
- util/helper/manager가 커진다.
- 메서드 이름에 and가 들어간다.
- 조건문이 정책을 숨긴다.

---

## 보류 기준

리팩터링 중 행위 변경이 필요해지면 멈춘다.
그 변경은 refactor가 아니라 feat 또는 fix 작업 카드로 분리해야 한다.
