# Final Checklist

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Review Agent는 승인 전에 아래를 확인한다.

---

## 출처 정본

[loop-scoring-criteria](../../../docs/workflow/loop-scoring-criteria.md)

## 산출물

- 작업 카드가 존재한다.
- 테스트 보고서가 존재한다.
- 구현 보고서가 존재한다.
- feature 카드의 리팩터링 보고서가 존재한다.
- 보고서 내용과 diff가 서로 맞다.

---

## 책임

- Presentation / Application / Domain / Infrastructure 책임이 섞이지 않았다.
- 검증 책임 위치가 하네스 기준과 맞다.
- Request DTO와 Command가 분리되어 있다.
- Repository 인터페이스와 구현 위치가 기준과 맞다.

---

## 테스트

- 실패 테스트가 먼저 작성됐다.
- 필요한 production class가 직접 테스트됐다.
- 기존 테스트가 통과한다.
- 테스트 더블 선택이 책임 기준과 맞다.

---

## 커밋

- 커밋이 test → feat → refactor 순서로 분리되어 있다.
- 각 단계의 커밋이 메서드(public behavior / 구조 개선) 단위로 나뉘어 있다.
- 커밋 메시지가 type(scope) 영어 + 한국어 summary 형식이다.
- 단계 보고서의 커밋 목록과 실제 이력이 일치한다.

---

## 완료

- Review 판정이 승인 또는 반려로 명확하다.
- 남은 위험과 범위 밖 발견 사항이 기록됐다.
- 영구화할 decision 후보가 있으면 오케스트레이터에게 보고됐다.
