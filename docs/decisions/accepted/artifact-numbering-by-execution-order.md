# Artifact Numbering By Execution Order

## 상태

accepted

---

## 문제 상황

산출물 체인의 파일 번호가 실행 순서와 어긋나면 게이트 판정이 깨진다.

예를 들어 `04-test-report`가 `03-review-report`보다 뒤 번호이면,
"테스트 보고서가 없으면 구현을 시작하지 않는다"는 게이트가
번호 순서와 모순되어 사람과 스크립트 모두 혼란한다.

TDD 흐름(Red → Green → Refactor → Review)이 번호만 봐서는 드러나지 않는다.

---

## 선택한 방향

산출물 번호는 **실행 순서(TDD 사이클)** 와 1:1로 일치시킨다.

    00-task-card.md                   오케스트레이터: 작업 정의 컴파일
    01-red-test-report.md             Test: 실패하는 테스트 (Red)
    02-green-implementation-report.md Feat: 최소 구현으로 통과 (Green)
    03-refactor-report.md             Refactor: 행위 보존 구조 개선
    04-review-report.md               Review: 승인 / 반려
    05-summary.md                     오케스트레이터: 통합·영구화 판단
    06-scorecard.md                   Review: 철학 점수표

게이트는 번호 순서와 일치한다.

- `00`이 없으면 `01`(Test)을 시작하지 않는다.
- `01`이 없으면 `02`(Feat)을 시작하지 않는다.
- feature 카드는 `03`(Refactor) 없이 `04`(Review)를 시작하지 않는다.
  개선할 것이 없으면 "개선 사항 없음 + 행위 보존 확인"을 기록한다.
- refactor 전용 카드는 `01` 없이 진행하고 `03`을 `02`와 동등하게 인정한다.
- `04`가 승인이어야 `05`(Summary)를 쓴다.

---

## 선택 이유

### 번호가 곧 흐름

번호만 봐도 Red → Green → Refactor → Review가 읽힌다.
파일명이 사이클을 설명하므로 별도 설명이 필요 없다.

### 게이트 결정성

번호 순서 = 게이트 순서이므로, 스크립트가 "직전 번호 산출물이 있는가"만
검사하면 된다. 순서 판정에 의미 해석이 들어가지 않는다.

---

## 트레이드오프

### 기존 번호 체계 마이그레이션

구 체계(`01-test-report`, `02-implementation-report`, `02-refactor-report`,
`03-review-report`, `04-summary`, `05-scorecard`)를 쓰는 스크립트·문서·history를
함께 갱신해야 한다. 한 번의 일괄 교체 비용이 있다.

---

## 현재 판단

design-note / implementation-plan 같은 사이클에 없는 단계는 별도 산출물 번호로
만들지 않고, 필요하면 `00-task-card.md` 안의 섹션으로 흡수한다.
산출물 번호는 실제 실행 단계에만 부여한다.

---

## 재검토 신호

- 사이클에 새 실행 단계가 추가되어 번호 사이에 삽입이 필요해진다.
- refactor 전용 카드 등 예외 경로가 번호 순서로 표현되지 않는 경우가 늘어난다.
