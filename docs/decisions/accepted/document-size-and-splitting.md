# Document Size And Splitting

## 상태

accepted

---

## 문제 상황

AI 에이전트는 필요한 컨텍스트를 빠르게 찾아야 한다.
router(AGENTS.md), adapter(CLAUDE/GEMINI/skills/cursor), role 정본이 비대해지면
정본 중복과 drift가 생기고, 약한 모델의 컨텍스트 예산을 넘긴다.

Markdown에는 **공식 글자 수 제한이 없다.** OpenAI/Anthropic/Google 같은 벤더의
공식 제한도 아니다. 그러나 하네스가 장기적으로 작고 찾기 쉬운 구조를 유지하려면
운영 기준이 필요하다.

---

## 선택한 방향

문서를 두 부류로 나눠 다르게 다룬다.

**router / adapter / role 문서 = hard limit (FAIL).** 짧아야 하는 진입·연결 문서.

    AGENTS.md                 ≤ 150 lines
    CLAUDE.md / GEMINI.md     ≤ 50 lines
    .claude/skills/*/SKILL.md ≤ 80 lines
    .cursor/rules/*.mdc       ≤ 80 lines
    agents/*/AGENTS.md        ≤ 150 lines

**일반 docs = soft limit (WARN).** 주제에 따라 길어질 수 있으므로 바로 FAIL하지 않는다.

    docs/**/*.md              > 20,000 chars
    docs/architecture/*.md    > 25,000 chars
    docs/**/*.md              > 100 lines 이고 Index/목차 없음
                              (단, docs/decisions/** 와 *index.md 는 면제 — 단일 주제)
    templates/*.md            > 10,000 chars
    docs/references/*-llms.txt> 10 KB

- adapter는 정본 복제를 금지한다(길어지면 복제 의심).
- 긴 정본은 의미 단위로 분리한다. 같은 주제를 여러 파일에 중복 설명하지 않는다.

---

## 분리 신호 (Split Signals)

아래 신호가 있으면 분리를 검토한다(자동 분리하지 않는다).

- 한 파일에 서로 다른 결정/원칙/절차가 섞여 있다.
- 특정 에이전트가 특정 섹션만 반복 참조한다.
- 변경 시 관련 없는 섹션까지 자주 수정된다.
- scorecard/review에서 "문서를 찾기 어렵다"는 신호가 반복된다.
- 문서가 20,000자를 넘었다.
- 100줄 이상인데 Index가 없다(가이드 문서).

---

## 선택 이유

### 진입·연결 문서는 짧아야 신호다

router/adapter/role은 "어디서 읽는가"를 가리키는 문서다. 길어지면 정본을
복제하거나 규칙을 흡수한 것이므로 FAIL로 즉시 막는다.

### 일반 docs는 WARN으로 운영

정본 docs는 주제 깊이에 따라 길 수 있다. 일괄 FAIL은 과도한 실패와
무리한 분리를 유발하므로 WARN으로 신호만 준다.

---

## 트레이드오프

- 파일 수가 늘어날 수 있다.
- WARN은 강제력이 없어 무시될 수 있다(그래서 hard는 FAIL로 분리).
- 줄 수/글자 수는 품질의 근사치일 뿐 정확한 지표가 아니다.

---

## 현재 판단

이번에는 기준과 검증 레일만 도입한다. 현재 docs를 대규모로 쪼개지 않는다.
WARN으로 드러난 긴 문서는 분리 신호가 함께 잡힐 때 개별적으로 검토한다.

---

## 강제

- `scripts/check-doc-size.sh` — hard=FAIL, soft=WARN
- `scripts/verify-harness.sh`가 마지막에 호출한다.

---

## 재검토 신호

- hard limit가 정당한 문서를 자주 막는다(한도가 너무 빡빡).
- WARN이 노이즈로 무시된다(대상 범위가 너무 넓다).
- 줄 수 기준이 의미 단위 분리와 어긋난다.
