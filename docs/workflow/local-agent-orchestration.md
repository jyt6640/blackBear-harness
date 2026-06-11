# Local Agent Orchestration

자동 로컬 에이전트 모드는 상위 오케스트레이터가 작업 카드를 컴파일하고,
Test → Feat → Refactor → Review 실행을 로컬 LLM 역할 에이전트에 위임하는 흐름이다.

---

## 전제

- 카드마다 하나의 branch와 worktree를 사용한다.
- worktree의 tracked 변경은 단계 시작 전에 깨끗해야 한다.
- provider, model, 주소, token은 저장소 밖 Codex profile에 둔다.
- 모델 context는 역할 하네스, 카드 지정 문서, 대상 코드를 처리할 만큼 충분해야 한다.
- 실제 카드 실행 전 `scripts/local-agent/check-provider.sh`로 연결과 tool use를 확인한다.

profile 예시:

```toml
model = "<model-id>"
model_provider = "local_llm"
model_context_window = 65536
approval_policy = "never"

[model_providers.local_llm]
name = "Local LLM"
base_url = "http://<host>:<port>/v1"
wire_api = "responses"
```

---

## 작업 카드 입력

오케스트레이터는 일반 작업 카드 항목과 함께 아래 목록을 컴파일한다.

- `필수 상위 문서`: 모든 역할이 공통으로 읽는 작업 관련 정본
- `역할별 추가 문서`: Test, Feat, Refactor, Review 각각에만 필요한 정본

전체 `docs/`를 목록에 넣지 않는다.
production code와 프로젝트 accepted decision에서 작업에 직접 필요한 문서만 고른다.

---

## 단계 실행

단일 단계 점검:

```bash
scripts/local-agent/run-stage.sh test <작업명> --profile <profile> --dry-run
```

전체 자동 릴레이:

```bash
scripts/local-agent/run-pipeline.sh <작업명> --profile <profile>
```

실행기는 단계마다 다음을 수행한다.

1. 역할 `enforce-workflow.sh`를 실행한다.
2. 카드 단위 lock으로 같은 카드의 동시 실행을 막는다.
3. 역할 하네스, 카드 지정 문서, 이전 산출물의 존재를 검사한다.
4. 격리된 `CODEX_HOME`과 최소 `AGENTS.md`를 만든다.
5. 역할 에이전트를 실행한다.
6. 새 보고서, 커밋 type, clean worktree를 검사한다.
7. 통과한 경우에만 다음 역할을 실행한다.

---

## Review 반려

Review Agent는 `03-review-report.md`의 `재실행 단계`에 `test`, `feat`,
`refactor` 중 하나를 기록한다.

파이프라인은 반려 시점의 HEAD를 새 Review 기준 commit으로 저장하고 지정 단계부터
다시 실행한다. 제한 횟수를 넘거나 재실행 단계가 없으면 자동화를 중단하고 상위
오케스트레이터에게 올린다.

---

## 권한 경계

- Test: 테스트와 `01-test-report.md`, test 커밋
- Feat: production code와 `02-implementation-report.md`, feat 커밋
- Refactor: 행위 변경 없는 코드와 `02-refactor-report.md`, refactor 커밋
- Review: `03-review-report.md`만 작성, 코드와 커밋 변경 금지

역할 에이전트는 decision을 만들거나 승격하지 않는다.
입력 충돌, pending 영역, 요구 모순은 보고서에 기록하고 종료한다.
