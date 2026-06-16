# VERSION

base harness 버전. 프로젝트는 자기 AGENTS.md의 base 선언에 이 값을 기록한다.
base가 갱신되면 프로젝트는 `harness-sync`로 diff를 재검토한다.

## 현재

- version: 2.0.0-draft
- codename: router + cross-tool adapters
- date: 2026-06-16

## 변경 요지 (2.x)

- AGENTS.md를 라우터로 슬림화 (규칙 본문은 정본으로 이주)
- one canon, N adapters: CLAUDE.md / .claude/skills / .cursor/rules / GEMINI.md
- 산출물 번호를 TDD 실행 순서로 재정렬 (00 card → 06 scorecard)
- templates/project-layout 으로 프로젝트 인스턴스 분리
