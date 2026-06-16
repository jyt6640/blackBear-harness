# AGENTS.md — <프로젝트명>

이 프로젝트는 backend-harness base를 따른다.

## base 선언

- base harness: <경로 또는 submodule>  (예: `.harness/` 또는 git submodule)
- base 버전: <VERSION.md의 값>

base의 `AGENTS.md`, `agents/`, `docs/`, `templates/`, `scripts/`를 정본으로 읽는다.
충돌 시 판단 우선순위는 base `AGENTS.md`를 따른다.

## 프로젝트 override

base 기본 입장과 다른 결정만 여기 링크한다. (없으면 "없음")

- (override 결정) — `docs/decisions/<...>.md`

## 프로젝트 사실

- 생성 문서: `docs/generated/`
- 진행 작업: `docs/exec-plans/active/`
