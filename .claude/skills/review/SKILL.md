---
name: review
description: TDD 사이클 4단계. 작업 diff를 카드와 하네스 기준으로 검증해 승인/반려(04-review-report)와 철학 점수표(06-scorecard)를 산출한다. 리팩터링이 끝났을 때, "/review" 또는 "리뷰하자"라고 할 때 사용한다.
---

# Review 역할 런처 (얇은 어댑터)

정본은 [agents/review/AGENTS.md](../../../agents/review/AGENTS.md)다. 규칙은 여기 복제하지 않는다.

1. 정본 [agents/review/AGENTS.md](../../../agents/review/AGENTS.md)를 읽는다.
2. 입력: `00`~`03` 산출물과 작업 diff.
3. 출력: `04-review-report.md` ([templates/04-review-report.md](../../../templates/04-review-report.md)) + `06-scorecard.md` ([templates/06-scorecard.md](../../../templates/06-scorecard.md)).
4. 반려면 `재실행 단계`를 기록한다. 승인이면 오케스트레이터가 05-summary로 마무리한다.
