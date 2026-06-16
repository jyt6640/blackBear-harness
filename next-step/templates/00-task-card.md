# 00 Task Card

## 작업명

-

## 작업 유형

- feature | fix | refactor | docs | chore

## 시작 기준 commit

- (카드 생성 시점의 git rev-parse HEAD — 커밋 체인 검사의 시작점)

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

## 적용 지침 (공통 기준)

모든 카드에 항상 적용되는 기준이다.

- production code와 프로젝트 accepted decision을 우선한다.
- Feat 단계는 `01-test-report.md` 없이 시작하지 않는다.
- 테스트는 production class의 public behavior 기준으로 작성한다.
- 한 커밋은 하나의 public behavior 또는 하나의 책임 변경만 포함한다. Test는 test 커밋만, Feat는 feat 커밋만, Refactor는 refactor 커밋만 만든다.
- Controller는 받고 / 위임 / 응답만 한다. 형식 검증(null/blank/형식)은 Request DTO(@NotNull/@NotBlank/@Pattern)나 값 객체에 둔다.
- 헤더 / 인증 검증 실패는 Controller에서 응답을 만들지 않고 예외를 던져 global 핸들러가 변환한다. 에러 봉투(code/errors)를 Controller에서 조립하지 않는다.

## 이번 작업 컴파일 규칙

오케스트레이터가 이번 작업에 적용되는 상위 docs 규칙을 카드별로 컴파일한다.
역할 에이전트는 이 규칙을 1차 구속 계약으로 따르고, 모호하면 출처 정본을 확인한다.
각 줄은 한 규칙 + 출처(decision / 문서 이름)를 괄호로 인용한다.
이 섹션을 템플릿 placeholder 그대로 두지 않는다.

- (컴파일 필요: 이번 작업에 적용되는 규칙을 출처와 함께 적는다)
- (컴파일 필요: 예 — Service는 흐름만 조율하고 판단은 Domain / Policy / Validator에 둔다 (service-orchestration-only))
- (컴파일 필요: 예 — 트랜잭션 경계는 Service에 둔다 (transaction-boundary-in-service))

## 필수 상위 문서

모든 역할이 공통으로 읽어야 하는 작업 관련 정본만 적는다.
전체 `docs/`를 넣지 않는다.

- `docs/...`

## 역할별 추가 문서

### Test

- `docs/...`

### Feat

- `docs/...`

### Refactor

- `docs/...`

### Review

- `docs/...`

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
- `02-refactor-report.md`가 작성되고 행위 보존이 확인됐다.
- `03-review-report.md`가 승인이다.
- 영구화할 decision / docs 후보가 정리됐다.
