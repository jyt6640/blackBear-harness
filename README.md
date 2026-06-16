# blackBear Base Harness

Spring 백엔드 기능 개발을 Test → Feat → Refactor → Review 릴레이로
오케스트레이션하는 base 하네스다.

판단 체계와 문서 구조는 [AGENTS.md](./AGENTS.md)에서 시작한다.
이 문서는 새 프로젝트에서 처음 사용할 때의 흐름만 다룬다.

---

## 0. 준비 (최초 1회)

새 프로젝트 저장소에서:

1. base 하네스를 참조할 수 있게 한다.
   같은 머신이면 경로 참조, 팀이면 저장소 복사 또는 서브모듈로 가져온다.
2. 스킬을 에이전트에 복사한다.
   - Claude Code: `.claude/skills/`
   - Codex: `~/.codex/skills/`
3. 강제 훅과 스크립트를 활성화한다.

       git config core.hooksPath .githooks

   커밋 메시지 형식이 이 시점부터 강제된다.

---

## 1. 프로젝트 하네스 만들기 (최초 1회)

    /harness-interview

- 규모 파악 5문항(사용자 규모, 수명, 기술 고정성, 동시성, 배포 형태)으로 프로파일이 정해진다.
- 프로파일에 맞는 결정 영역 질문에 답하면 draft decision이 만들어지고,
  확인을 거쳐 프로젝트 AGENTS.md(base diff), decisions/, development-guideline.md가 생긴다.
- 모르는 질문은 pending, 해당 없는 질문은 not-applicable로 남긴다.

이 단계가 끝나면 생성 문서는 다시 읽지 않는다.
이후 모든 기능 개발은 이 프로젝트 하네스 위에서 돌고, 같은 질문을 반복하지 않는다.

기존 코드가 있는 프로젝트라면 인터뷰가 코드를 먼저 관찰하고 확인 질문으로 바뀐다.

---

## 2. 기능 개발 릴레이

기능 하나의 전체 흐름이다.
각 단계는 끝나면 멈추고, 다음 단계는 사용자가 직접 호출해야 한다.

### /orchestrate — 시작

    /orchestrate 주문 생성이랑 주문 취소 만들어줘

- 카드가 2장 이상이면 `next-step/work/backlog.md`에 카드 목록과 순서를 먼저 기록한다.
- 첫 카드만 `next-step/work/<작업명>/00-task-card.md`로 컴파일한다.
  (대상 행위, 적용 지침, 금지, 완료 기준, 시작 기준 commit)
- 정지: "작업 카드 작성 완료. /test-agent(사용자 릴레이) 또는 /local-agent(자동 릴레이)를 실행해주세요."

### /test-agent — 실패 테스트

- 카드의 행위를 public behavior 단위 실패 테스트로 작성하고 실패를 실행으로 확인한다.
- public behavior 단위로 `test(scope):` 커밋을 만든다.
- `01-test-report.md` 작성 후 정지: "/feat-agent를 실행해주세요."

### /feat-agent — 최소 구현

- 실패 테스트를 통과시키는 최소 구현. 테스트는 절대 수정하지 않는다.
- 행위 하나당 `feat(scope):` 커밋 하나.
- `02-implementation-report.md` 작성 후 정지: "/refactor-agent를 실행해주세요."

### /refactor-agent — 구조 개선

- 행위 변경 없는 구조 개선만. 구조 개선 하나당 `refactor(scope):` 커밋 하나.
- 개선할 것이 없으면 "개선 사항 없음"과 행위 보존 확인을 기록한다.
- `02-refactor-report.md` 작성 후 정지: "/review-agent를 실행해주세요."

### /review-agent — 검증

- 게이트가 산출물 체인과 커밋 체인(형식 / type 제한 / test → feat → refactor 순서)을 검사한다.
- 하네스 기준(책임 경계, 테스트, 커밋 단위)으로 승인 / 반려를 판정한다.
- `03-review-report.md` 작성 후 정지.
  - 승인 → "/orchestrate로 마무리를 진행해주세요."
  - 반려 → 구현 문제는 /feat-agent, 구조 문제는 /refactor-agent, 행위 정의 문제는 /test-agent.

### /orchestrate — 마무리

- `04-summary.md` 작성, 백로그 상태 갱신.
- 영구화할 결정만 decisions / docs / 커밋 메시지로 승격한다.
- `work/<작업명>`은 삭제하지 않고 `history/<작업명>/`으로 이동해 완료 기록으로 보존한다.
- 백로그에 다음 카드가 있으면 정지: "다음 카드입니다. /orchestrate를 실행해주세요."
- 모든 카드가 끝나면 `work/backlog.md`도 `history/<백로그명>-backlog.md`로 이동한다.

---

## 3. 진행 중 규칙

- 단계를 건너뛰면 각 역할의 `enforce-workflow.sh` 게이트가 진행을 거부한다.
- 같은 고민이 반복되거나, 코드와 문서가 충돌하거나, pending 영역에 들어가면 → `/draft-decision`
- 세션이 끊겨도 `next-step/work/`의 백로그와 보고서만 읽으면 그 지점에서 이어진다.
- base 하네스가 갱신되면 → `/harness-sync`

---

## 치트시트

| 상황 | 호출 |
|---|---|
| 프로젝트 처음 시작 (하네스 없음) | `/harness-interview` |
| 기능 개발 시작 / 다음 카드 / 마무리 | `/orchestrate` |
| 실패 테스트 작성 | `/test-agent` |
| 최소 구현 | `/feat-agent` |
| 구조 개선 | `/refactor-agent` |
| 검증과 승인 / 반려 | `/review-agent` |
| 카드 전체를 로컬 LLM 자동 릴레이로 실행 | `/local-agent` |
| 결정 기록 | `/draft-decision` |
| base 갱신 반영 | `/harness-sync` |

기능 하나 = 인터뷰 0회(이미 있으니) + 카드당 스킬 호출 6번.
사용자 릴레이 모드에서는 모든 전환점에서 시스템이 멈추고 다음 단계는 사용자가 호출한다.

---

## 4. 자동 로컬 에이전트 모드 (/local-agent)

카드 작성 후 Test → Feat → Refactor → Review를 사용자가 단계마다 호출하는 대신,
로컬 LLM 역할 에이전트에 맡겨 한 번에 직렬 실행할 수 있다.

### 준비 (최초 1회)

머신별 provider 주소, 모델, token은 저장소에 넣지 않고
Codex profile(`~/.codex/<profile>.config.toml`)로 관리한다.
형식은 [docs/workflow/local-agent-orchestration.md](./docs/workflow/local-agent-orchestration.md)의 예시를 따른다.

### 사용

    /orchestrate 주문 취소 만들어줘
    → 카드 컴파일 (자동 모드용 필수 상위 문서 / 역할별 추가 문서 포함) → 정지

    /local-agent
    → provider 검증(check-provider) → dry-run 점검 → run-pipeline 실행

파이프라인이 단계마다 자동으로 검사한다.

- 역할 게이트(enforce-workflow)와 산출물 체인
- 단계별 커밋 type (test / feat / refactor, Review는 커밋 금지)
- ignored 파일을 제외한 tracked/untracked clean worktree
- 단계별 금지 source 경로와 실제 참조 문서
- Feat / Refactor 완료 후 프로젝트 `verify.sh` green-bar

역할 에이전트가 읽는 입력은 카드가 지정한 것뿐이다:
자기 역할 AGENTS.md + 역할 docs + 카드의 `필수 상위 문서` / `역할별 추가 문서` + 이전 산출물.
전체 docs를 읽지 않는다.

### 반려와 중단

- Review 반려 → `03-review-report.md` frontmatter의 `restart_stage`부터 제한 횟수 안에서 자동 재실행
- 한도 초과 또는 BLOCKED → 멈추고 사용자에게 보고.
  막힌 단계부터 사용자 릴레이(/test-agent 등)로 이어받을 수 있다.
- 승인 → /orchestrate로 마무리 (04-summary, history 이동)는 동일하다.

스크립트를 직접 쓸 수도 있다.

```bash
scripts/local-agent/check-provider.sh --profile <profile>
scripts/local-agent/run-stage.sh test <작업명> --profile <profile> --dry-run
scripts/local-agent/run-pipeline.sh <작업명> --profile <profile>
scripts/local-agent/run-pipeline.sh <작업명> --profile <profile> --hybrid  # 구현=로컬, Review=강모델
scripts/local-agent/run-backlog.sh --profile <profile> --parallel 2        # 백로그 무인 순회 + worktree 병렬
```
