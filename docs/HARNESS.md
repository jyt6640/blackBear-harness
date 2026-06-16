# HARNESS.md — base harness 적용 가이드

이 저장소는 실제 앱이 아니라 **여러 Spring 백엔드 프로젝트에 적용하는 base harness**다.
새 프로젝트가 이 base를 어떻게 입히는지, 무엇을 채우고 무엇을 덮어쓰지 않는지 정의한다.

---

## 1. 정본과 어댑터 (one canon, N adapters)

→ [cross-tool-adapter-layer](decisions/accepted/cross-tool-adapter-layer.md)

**정본 (도구 무관, 한 벌):**

- `AGENTS.md` — 라우터 진입점
- `agents/` — 역할 정본
- `docs/` — 규칙·가치·결정·참조
- `templates/` — 산출물 정본
- `scripts/` — 기계 강제

**어댑터 (도구별, 얇은 포인터):** `CLAUDE.md`, `.claude/skills/`, `.cursor/rules/`, `GEMINI.md`.
어댑터는 정본을 복제하지 않고 경로만 가리킨다. `scripts/check-adapter-sync`가 검사한다.

---

## 2. 철학 중복 방지 규칙

같은 주제의 철학을 두 곳에 쓰지 않는다.

- `docs/principles/` = 철학 **정본**.
- `agents/<role>/docs/` = 그 역할의 **실행 체크리스트·절차**만. 철학을 다시 쓰지 않고 정본을 인용한다.

역할 docs에서 규칙 본문을 발견하면 정본으로 옮기고 인용으로 되돌린다.

---

## 3. 새 프로젝트에 적용하는 법

세 방식 중 프로젝트 상황에 맞게 고른다.

### A. submodule (base 갱신을 추적하고 싶을 때 — 권장)

    git submodule add <base-harness-repo> .harness
    cp -r .harness/templates/project-layout/* .
    # HARNESS_DIR=.harness 로 verify가 base scripts를 호출

base가 갱신되면 `git submodule update --remote` 후 `harness-sync`로 diff 검토.

### B. 복사 (base와 분리해 자유롭게 수정할 때)

    cp -r <base-harness>/* <project>/
    # 이후 base 갱신은 수동 반영

### C. project-layout만 인스턴스화 (base는 별도 위치 참조)

    cp -r <base-harness>/templates/project-layout/* .
    # AGENTS.md의 'base 선언'에 base 경로/버전 기록

---

## 4. 채우는 것 / 덮어쓰지 않는 것

| 프로젝트가 채운다 | base 정본 (덮어쓰지 않는다) |
| --- | --- |
| `docs/generated/` (코드에서 생성) | `docs/principles/`, `docs/architecture/` |
| `docs/exec-plans/active|completed/` | `agents/`, `templates/`, `scripts/` |
| 프로젝트 `docs/decisions/` (override) | base `docs/decisions/` |
| `ARCHITECTURE.md`, `AGENTS.md`(base 선언+override) | base `AGENTS.md` |

base 규칙을 바꾸려면 base 파일을 고치지 말고 **프로젝트 `docs/decisions/`에 override 결정**을 쓴다.
판단 우선순위상 프로젝트 결정이 base 기본 입장을 이긴다 → base `AGENTS.md`.

---

## 5. 버전

base 버전은 [VERSION.md](../VERSION.md)에 있다.
프로젝트 `AGENTS.md`의 base 선언 버전과 실제 base 버전이 다르면 `harness-sync`로 재검토한다.

---

## 6. 문서 크기/분리 기준

공식 글자 수 제한은 없다(벤더 제한 아님). 아래는 backend-harness가 작고 찾기 쉬운
구조를 유지하기 위한 **운영 기준**이다. 정본·강제는
[document-size-and-splitting](decisions/accepted/document-size-and-splitting.md),
검사는 `scripts/check-doc-size.sh`(verify-harness가 호출).

- router(AGENTS.md)와 adapter(CLAUDE/GEMINI/skills/cursor)는 짧아야 한다. 초과는 FAIL.
- role AGENTS.md가 길어지면 실행 절차를 `agents/<role>/docs/`로 분리한다. 초과는 FAIL.
- 정본 docs는 의미 단위로 분리한다. 자주 읽는 문서는 작고 찾기 쉬워야 한다.
- 100줄 이상 가이드 문서는 Index를 두거나 분리를 검토한다(WARN, decisions는 단일 주제라 면제).
- 20,000자 이상 문서는 분리를 검토한다(WARN).
- adapter가 길어지면 정본 복제 가능성을 의심한다.
- 같은 주제를 여러 파일에 중복 설명하지 않는다.

분리 신호와 한도 표는 decision 문서에 있다. 이번에는 기준과 검증 레일만 두고,
현재 docs를 대규모로 쪼개지 않는다.
