# agents/ — 역할 정본

각 역할의 정체성·책임·입력·출력·금지·완료 기준을 정의하는 **정본**이다.
도구 무관하다. 어댑터(`.claude/skills/`, `.cursor/rules/`)는 이 디렉토리를 가리키기만 한다.

| 역할 | 정본 | 산출물 |
| --- | --- | --- |
| Test | [test/AGENTS.md](test/AGENTS.md) | 01-red-test-report |
| Feat | [feat/AGENTS.md](feat/AGENTS.md) | 02-green-implementation-report |
| Refactor | [refactor/AGENTS.md](refactor/AGENTS.md) | 03-refactor-report |
| Review | [review/AGENTS.md](review/AGENTS.md) | 04-review-report + 06-scorecard |
| Prompt Improver | [prompt-improver/AGENTS.md](prompt-improver/AGENTS.md) | Prompt Improvement Proposal (proposal-only) |

각 역할은 goal 기반 루프로 작업한다(실행 방식). 루프 정의·반복 한도의 단일 출처는
[loop-engineering](../docs/workflow/loop-engineering.md)이고, 역할 정본은 이를 참조한다.
prompt-improver는 TDD 릴레이 단계가 아니라 약점을 개선안화하는 메타 역할이며,
기본 proposal-only — 명시 승인 후에만 정본을 고친다.

## skill 실행 진입점

    Claude Skill 실행 → .claude/skills/<skill>/SKILL.md(얇은 어댑터)
    → agents/<role>/AGENTS.md(도구 무관 역할 정본)를 읽고 실행
    → 전체 라우터는 루트 AGENTS.md

어댑터는 정본 경로만 가리킨다. 규칙·철학을 어댑터에 복제하지 않는다.

## 역할 docs 규칙

`agents/<role>/docs/`에는 **실행 체크리스트·절차**만 둔다.
철학 본문은 `docs/principles/`(정본)에 두고 역할 AGENTS가 링크로 참조한다.
같은 주제를 두 곳에 쓰지 않는다. → [docs/HARNESS.md](../docs/HARNESS.md) 2절
