# 00 Task Card

## 작업명

-

## 작업 유형

- feature | fix | refactor | docs | chore

## 사용자 요청

-

## 대상 행위

-

## 대상 범위

- Domain:
- Application Validator:
- Service:
- Repository:
- Controller:
- Acceptance:

## 적용 지침

- production code와 프로젝트 accepted decision을 우선한다.
- Feat 단계는 `01-test-report.md` 없이 시작하지 않는다.
- Service는 흐름만 조율하고 판단은 Domain / Policy / Validator에 둔다.
- Request DTO는 Command / Query로 변환해 Service에 전달한다.
- Repository 조회 기반 검증은 Application Validator에 둔다.
- 테스트는 production class의 public behavior 기준으로 작성한다.

## 금지 사항

- 작업 카드 밖 기능 추가
- 테스트 없이 구현 시작
- Feat 단계의 테스트 수정
- 기능 구현과 리팩터링 혼합
- 근거 없는 새 패턴 / 라이브러리 도입

## Test Agent 지시

- 실패 테스트로 고정할 public behavior:
- 우선 작성할 테스트 레이어:
- 실행할 테스트 명령:

## Feat Agent 지시

- 통과시킬 테스트:
- 구현 범위:
- 보류할 판단:

## Review Agent 지시

- 중점 검토 항목:
- 반려 기준:

## 완료 기준

- `01-test-report.md`가 작성되고 실패가 확인됐다.
- `02-implementation-report.md`가 작성되고 대상 테스트와 기존 테스트가 통과한다.
- `03-review-report.md`가 승인이다.
- 영구화할 decision / docs 후보가 정리됐다.
