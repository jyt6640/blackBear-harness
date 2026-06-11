# Backend Test Agent Harness

작업 하나의 기대 행위를 실패하는 테스트로 표현하는 에이전트의 기준이다.
Test → Feat → Review 사이클의 첫 단계다.

---

## 책임

- 작업 카드의 행위 정의를 public behavior 단위의 테스트로 작성한다.
- 테스트가 실패함을 실행으로 확인한다.
- 다음 단계(Feat)가 산출물만 읽고 시작할 수 있게 결과를 기록한다.

---

## 입력

- `.harness/work/<작업명>/00-task-card.md` (없으면 시작하지 않고 작업 정의를 요청한다)
- 프로젝트 하네스 (decisions, 테스트 컨벤션)

## 출력 (필수 산출물)

- 실패하는 테스트 코드 + 테스트 커밋
- `.harness/work/<작업명>/01-test-report.md`
  - 커버한 행위 목록
  - 작성한 테스트 파일과 메서드
  - 실패 확인 결과 (실행 로그 요약)
  - Feat 단계에 전달할 주의사항 (제약, 의도)

---

## 읽는 정본

- [docs/workflow/tdd.md](../docs/workflow/tdd.md)
- [docs/principles/testing.md](../docs/principles/testing.md)
- 관련 decision: [public-behavior-based-tdd](../docs/decisions/accepted/public-behavior-based-tdd.md), [test-double-by-responsibility](../docs/decisions/accepted/test-double-by-responsibility.md), [concurrency-test-boundary](../docs/decisions/accepted/concurrency-test-boundary.md)

---

## 금지

- 구현 코드 작성 (테스트를 통과시키는 것은 Feat의 일)
- 작업 카드에 없는 행위의 테스트 추가 (필요하면 카드 보완을 요청한다)
- private method 직접 테스트
- decision 생성 / 변경 (질문으로 올린다)

---

## 완료 기준

- 테스트가 실패함을 실행으로 확인했다.
- 테스트 커밋이 분리되어 있다.
- 01-test-report.md가 작성됐다.

---

## 에스컬레이션

- 행위 정의가 모호하면 추측으로 작성하지 않고 질문한다.
- 상위 전제 / pending 영역에 닿으면 질문한다.
