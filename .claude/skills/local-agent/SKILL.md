---
name: local-agent
description: 작업 카드의 Test → Feat → Refactor → Review를 로컬 LLM 역할 에이전트에 위임해 자동 릴레이로 실행한다. 사용자가 "로컬 에이전트로 돌려줘", "자동 릴레이", "/local-agent"라고 할 때, 또는 카드 작성 후 자동 모드를 선택했을 때 사용한다.
---

# Local Agent 자동 릴레이 실행

이 스킬은 얇은 런처다. 절차의 정본은 문서에 있다.

1. 정본을 읽는다.
   - [docs/workflow/local-agent-orchestration.md](../../../docs/workflow/local-agent-orchestration.md) — 전제, 카드 입력 계약, 반려 처리, 권한 경계
   - [AGENTS.md](../../../AGENTS.md)의 자동 로컬 에이전트 모드
   (base 저장소 밖이면 프로젝트 AGENTS.md의 base 선언 경로에서 찾는다)

2. 전제를 확인한다.
   - 작업 카드가 있고 `필수 상위 문서` / `역할별 추가 문서` 목록이 채워져 있어야 한다.
     없으면 /orchestrate로 카드를 먼저 컴파일한다.
   - profile 이름을 확인한다 (`~/.codex/<profile>.config.toml`).
     머신별 provider / model / 주소는 저장소에 넣지 않는다.
   - 첫 실행이거나 provider가 바뀌었으면 연결을 검증한다.

         scripts/local-agent/check-provider.sh --profile <profile>

3. 입력 계약을 dry-run으로 점검한 뒤 자동 릴레이를 실행한다.

       scripts/local-agent/run-stage.sh test <작업명> --profile <profile> --dry-run
       scripts/local-agent/run-pipeline.sh <작업명> --profile <profile>

   비용과 독립 리뷰를 함께 얻으려면 하이브리드로 실행한다 (구현은 로컬, Review는 강모델):

       scripts/local-agent/run-pipeline.sh <작업명> --profile <profile> --hybrid

   하이브리드는 Test/Feat/Refactor 후 멈춘다. 이어서 /review를 실행해
   강모델이 04-review-report.md와 06-scorecard.md를 작성한다 (자기 채점 편향 제거).
   → docs/decisions/accepted/hybrid-execution-mode.md

   실행기가 단계마다 게이트, 커밋 type, clean worktree를 검사한다.
   실행 중 단계를 직접 대행하지 않는다. 실패하면 실행기의 FAIL 메시지를 그대로 보고한다.

4. 결과를 보고하고 멈춘다.
   - Review 승인 → 사용자에게 /orchestrate 마무리(05-summary, history 이동)를 요청한다.
   - 재시도 한도 초과 또는 BLOCKED → 보고서 내용과 함께 사용자에게 판단을 요청한다.
     로컬 에이전트가 막힌 단계를 사용자 릴레이(/test 등)로 이어받을 수 있다.
