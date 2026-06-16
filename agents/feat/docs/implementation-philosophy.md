# Feat — 실행 체크리스트

> 역할 실행 문서다. 철학 본문은 여기 쓰지 않는다.
> 관련 철학·판단 기준은 docs/principles/, docs/workflow/를 참조한다.

## 참조

철학은 정본에서 읽는다(여기 재서술하지 않는다):
[oop](../../../docs/principles/oop.md), [method-design](../../../docs/principles/method-design.md), 기본 입장은 [decisions](../../../docs/decisions)

---

## 구현 범위 (체크리스트)

- [ ] `01-red-test-report.md`의 실패 테스트를 통과시키는 범위로만 구현했는가
- [ ] 테스트가 추가 요구를 암시해도 작업 카드에 없는 행위는 구현하지 않았는가
- [ ] 현재 코드에 없는 패턴/라이브러리/프레임워크를 근거 없이 도입하지 않았는가
- [ ] test 커밋 → feat 커밋 순서를 지켰는가

## 판단 보류

상위 전제나 pending decision 영역에 닿으면 임의로 결정하지 않고 멈춰 질문한다.
예: 인증 방식, Domain / Persistence Entity 분리, 이벤트 기반 처리, Aggregate 경계, Repository 예외 계약.
