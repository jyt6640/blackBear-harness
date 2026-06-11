# Final Checklist

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


Review Agent는 승인 전에 아래를 확인한다.

---

## 산출물

- 작업 카드가 존재한다.
- 테스트 보고서가 존재한다.
- 구현 보고서가 존재한다.
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

## 완료

- Review 판정이 승인 또는 반려로 명확하다.
- 남은 위험과 범위 밖 발견 사항이 기록됐다.
- 영구화할 decision 후보가 있으면 오케스트레이터에게 보고됐다.
