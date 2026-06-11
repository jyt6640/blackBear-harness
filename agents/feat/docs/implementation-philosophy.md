# Implementation Philosophy

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Feat Agent의 구현은 실패 테스트를 통과시키는 최소 변경이다.
구조 기준을 지키되, 작업 카드 밖의 리팩터링이나 미래 확장은 하지 않는다.

---

## 핵심 방향

- 현재 프로젝트의 production code와 accepted decision을 우선한다.
- Service는 흐름만 조율하고 판단은 Domain / Policy / Validator에 둔다.
- Domain은 기술 구현을 알지 않는다.
- Request DTO와 Command를 분리한다.
- 현재 코드에 없는 패턴, 라이브러리, 프레임워크를 근거 없이 도입하지 않는다.

---

## 구현 범위

구현은 `01-test-report.md`의 실패 테스트를 통과시키는 범위로 제한한다.
테스트가 추가 요구를 암시하더라도 작업 카드에 없는 행위는 구현하지 않는다.

---

## 판단 보류

상위 전제나 pending decision 영역에 닿으면 임의로 결정하지 않는다.
예: 인증 방식, Domain / Persistence Entity 분리, 이벤트 기반 처리, Aggregate 경계, Repository 예외 계약.
