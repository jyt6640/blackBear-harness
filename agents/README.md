# agents/ — 역할 정본

각 역할의 정체성·책임·입력·출력·금지·완료 기준을 정의하는 **정본**이다.
도구 무관하다. 어댑터(`.claude/skills/`, `.cursor/rules/`)는 이 디렉토리를 가리키기만 한다.

| 역할 | 정본 | 산출물 |
| --- | --- | --- |
| Test | [test/AGENTS.md](test/AGENTS.md) | 01-red-test-report |
| Feat | [feat/AGENTS.md](feat/AGENTS.md) | 02-green-implementation-report |
| Refactor | [refactor/AGENTS.md](refactor/AGENTS.md) | 03-refactor-report |
| Review | [review/AGENTS.md](review/AGENTS.md) | 04-review-report + 06-scorecard |

## 역할 docs 규칙

`agents/<role>/docs/`에는 **실행 체크리스트·절차**만 둔다.
철학 본문은 `docs/principles/`(정본)에 두고 인용한다. 같은 주제를 두 곳에 쓰지 않는다.
→ [docs/HARNESS.md](../docs/HARNESS.md) 2절
