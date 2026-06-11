# Rejection Criteria

> 이 문서는 공유 정본의 실행 관점 요약이다.
> [docs](../../../docs)의 정본, decisions와 충돌하면 정본이 우선한다.


아래 항목은 Review Agent의 반려 기준이다.

---

## 산출물 위반

- `00-task-card.md` 없이 진행했다.
- Feat가 `01-test-report.md` 없이 시작됐다.
- Review가 `02-implementation-report.md` 없이 시작됐다.
- 실패 확인 로그가 없다.

---

## 테스트 위반

- 테스트가 public behavior가 아니라 private method를 검증한다.
- Service 테스트가 Domain / Policy / Validator 책임 검증을 대체한다.
- Feat 단계에서 테스트 기대값을 바꿨다.
- 동시성 위험을 Mock / Fake만으로 검증했다.

---

## 구조 위반

- Controller가 Repository 또는 Domain 상태 변경을 직접 수행한다.
- Service가 비즈니스 판단을 직접 수행한다.
- Domain이 기술 구현을 안다.
- Infrastructure가 비즈니스 규칙을 새로 만든다.
- Helper / Util / Manager로 책임을 흐린다.

---

## 범위 위반

- 작업 카드 밖 기능이 추가됐다.
- 기능 구현과 리팩터링이 섞였다.
- 새 패턴이나 라이브러리가 근거 없이 도입됐다.
