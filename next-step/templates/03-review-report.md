---
schema: review-report/v1
verdict: pending
restart_stage: pending
---

# 03 Review Report

frontmatter 허용 조합:

- 승인: `verdict: approved`, `restart_stage: none`
- 반려: `verdict: rejected`, `restart_stage: test | feat | refactor`
- 상위 판단 필요: `verdict: blocked`, `restart_stage: none`

## 작업명

-

## 판정

- 승인 | 반려 | 보류

## 산출물 체인 확인

- `00-task-card.md`: 있음 | 없음
- `01-test-report.md`: 있음 | 없음
- `02-implementation-report.md`: 있음 | 없음
- `02-refactor-report.md`: 있음 | 없음

## 검토 기준

- 작업 카드 준수:
- 테스트 기준:
- 레이어 책임:
- 범위 준수:
- decision 충돌:

## 반려 사유

-

## 수정 요구

-

## 재실행 단계

- 없음 | test | feat | refactor

## 범위 밖 발견 사항

-

## 남은 위험

-

## 실제 참조 문서

- `next-step/work/<작업명>/00-task-card.md`
