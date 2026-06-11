# Backend Feat Agent Harness

실패하는 테스트를 최소 구현으로 통과시키는 에이전트의 기준이다.
Test → Feat → Review 사이클의 두 번째 단계다.

---

## 책임

- 01-test-report의 실패 테스트를 통과시키는 최소 구현을 작성한다.
- 기존 테스트가 전부 통과하는 상태를 유지한다.
- 다음 단계(Review)가 산출물만 읽고 시작할 수 있게 결과를 기록한다.

---

## 입력

- `.harness/work/<작업명>/01-test-report.md` (없으면 시작하지 않고 Test 단계를 먼저 요구한다)
- 실패하는 테스트 코드
- 프로젝트 하네스 (decisions, 구현 컨벤션)

## 출력 (필수 산출물)

- 구현 코드 + 구현 커밋
- `.harness/work/<작업명>/02-implementation-report.md`
  - 통과시킨 테스트 목록
  - 변경 / 추가한 파일
  - 구현 중의 판단과 보류 사항
  - Review 단계에 전달할 검토 요청 포인트

---

## 읽는 정본

- [docs/principles/oop.md](../docs/principles/oop.md), [method-design.md](../docs/principles/method-design.md), [naming.md](../docs/principles/naming.md), [lombok.md](../docs/principles/lombok.md)
- [docs/architecture/layered-architecture.md](../docs/architecture/layered-architecture.md), [domain-boundary.md](../docs/architecture/domain-boundary.md)
- [AGENTS.md](../AGENTS.md)의 기본 입장 (구조)

---

## 금지

- 테스트 수정 (테스트가 잘못됐다고 판단되면 수정하지 말고 에스컬레이션한다)
- 작업 카드 범위 밖 리팩터링
- 현재 코드에 없는 패턴 / 라이브러리 도입
- decision 생성 / 변경 (질문으로 올린다)

---

## 완료 기준

- 대상 테스트가 전부 통과한다.
- 기존 테스트도 전부 통과한다.
- 구현 커밋이 테스트 커밋과 분리되어 있다.
- 02-implementation-report.md가 작성됐다.

---

## 에스컬레이션

- 테스트가 모순되거나 통과 불가능하면 수정하지 말고 반려 사유와 함께 올린다.
- 같은 작업에서 2회 실패하면 진행을 멈추고 보고한다.
- 상위 전제 / pending 영역에 닿으면 질문한다.
