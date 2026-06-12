# 05 Scorecard

이 점수표의 항목이 철학 채점 기준의 정본이다.
항목을 바꾸려면 이 템플릿을 바꾸고, 출처 정본과 함께 갱신한다.

## 작업명

-

## 채점 규칙

- 2: 준수
- 1: 부분 위반 (근거에 사유를 적는다)
- 0: 위반
- N/A: 이 카드에 해당 없음

점수는 diff와 단계 보고서를 근거로 매긴다. 인상이 아니라 코드 위치를 인용한다.

## 점수표

| ID | 항목 | 출처 | 점수 | 근거 |
|---|---|---|---|---|
| S1 | 레이어 책임 분리 (Controller / Service / Domain / Infra 침범 없음) | layered-architecture | | |
| S2 | Service는 흐름만 조율, 판단은 Domain / Policy / Validator에 위임 | service-orchestration-only | | |
| S3 | 검증 책임 위치 (null/blank는 DTO, 자기 상태는 Domain, 조회 기반은 Validator) | domain-boundary | | |
| S4 | Domain의 기술 독립 (기술 어노테이션 / 외부 호출 없음) | domain-does-not-know-technology | | |
| S5 | 생성 경로 통제 (생성자 닫고 정적 팩터리) | static-factory-method | | |
| S6 | Request / Command 분리 | request-command-separation | | |
| S7 | 예외의 의미 변환과 위치 (기술 예외 → 저장소 의미 → 유스케이스 의미) | infrastructure-exception-translation | | |
| T1 | public behavior 단위 직접 테스트 (Service 테스트로 대체 없음) | public-behavior-based-tdd | | |
| T2 | 테스트 더블을 책임 기준으로 선택 (Service=Mock, Validator=Fake 우선) | test-double-by-responsibility | | |
| T3 | 실패 확인 선행 (01 보고서에 실패 근거 존재) | tdd | | |
| R1 | 의도가 드러나는 네이밍, Helper / Util / Manager 없음 | naming, common-util-package | | |
| R2 | 메서드 설계 (한 의도, early return, boolean 정책 분기 없음) | method-design | | |
| P1 | 커밋이 메서드 단위, test → feat → refactor 순서 | git-convention | | |
| P2 | 카드 범위 준수 (범위 밖 변경 / 관련 없는 리팩터링 없음) | 불변 철학(최소 변경) | | |

## 합계

- 채점 항목 수 (N/A 제외):
- 총점 / 만점:

## 최저 항목 메모

가장 낮은 항목과 그 원인이 지침 부족인지, 지침 위반인지 구분해서 적는다.

-
