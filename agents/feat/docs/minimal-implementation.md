# Minimal Implementation

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


최소 구현은 대충 구현한다는 뜻이 아니다.
현재 실패 테스트를 통과시키면서 하네스의 책임 경계를 지키는 가장 작은 변경이다.

---

## 출처 정본

[explicit-over-reuse](../../../docs/decisions/accepted/explicit-over-reuse.md)

## 허용

- 실패 테스트를 통과시키는 production code 추가
- 기존 구조에 맞춘 작은 메서드 추출
- 명확한 이름을 위한 국소적 rename
- 필요한 테스트 실행과 보고서 작성

---

## 금지

- 테스트 수정
- 작업 카드 밖 기능 추가
- "나중에 필요할 것" 같은 선제 추상화
- 공통화만을 위한 Util / Helper / Manager 추가
- 리팩터링과 기능 구현을 한 단계에 섞기

---

## 구현 보고

`02-implementation-report.md`에는 무엇을 바꿨는지만 쓰지 않는다.
왜 그 책임 위치가 자연스러운지, 어떤 판단을 보류했는지, Review가 봐야 할 위험이 무엇인지 기록한다.
