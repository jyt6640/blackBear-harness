# Enforcement By Script

## 상태

accepted

---

## 문제 상황

md 문서는 지침일 뿐 강제력이 없다.

이 저장소에서도 같은 규칙이 여러 문서에 복제되어 어긋나거나,
pending 결정이 본문에 고정 규칙으로 박히는 일이 실제로 발생했다.

지침 위반을 사람이 눈으로 잡는 구조는 반복 비용이 크고 누락된다.

---

## 선택한 방향

규칙을 두 종류로 나눈다.

- 기계적으로 판정 가능한 규칙 → 스크립트로 강제한다.
  커밋 전에 실행되고, 실패하면 커밋이 막힌다.
- 맥락과 의도 판단이 필요한 규칙 → md 지침으로 유지한다.

분류 기준: 위반 여부를 맥락 해석 없이 판정할 수 있는가?
YES면 스크립트, NO면 지침이다.

---

## 현재 강제 항목

검사를 빠른 커밋 전 검사와 전체 하네스 검사로 나눈다.

- `.githooks/pre-commit` → `scripts/verify-fast.sh`
- CI 또는 수동 전체 검사 → `scripts/verify-harness.sh`

- 상대 링크 무결성
- decision 상태와 디렉토리 일치, 상태 섹션 존재
- 프로젝트 종속 누수 금지 패턴
- decisions의 프로젝트 기록 화법 재발 방지 (정본 화법: "이 하네스는")
- PHILOSOPHY_QNA_DRAFT.md 커밋 방지
- AGENTS.md 필수 섹션 존재

빠른 검사는 staged whitespace, 작업 메모리 추적, 미확정 QnA 추적과 필수 스크립트
실행 권한만 확인한다. 링크, decision, 역할 docs, history와 scorecard 정합성은 전체
검사에서 확인한다.

커밋 컨벤션도 스크립트가 검사한다.

- 커밋 메시지 형식: `scripts/check-commit-message.sh` (`.githooks/commit-msg`)
- 카드 릴레이의 커밋 type 제한과 test → feat → refactor 순서: `scripts/check-commit-chain.sh`
- public behavior/책임 단위 여부는 기계 판정 불가 영역이므로 Review Agent 지침으로 남긴다.

훅 활성화:

    git config core.hooksPath .githooks

---

## 선택 이유

### 드리프트의 실증

이 저장소의 정리 과정에서 발견된 문제(경로 불일치, pending의 규칙화,
목록 복제)는 전부 기계 판정 가능한 위반이었다.
지침만으로는 같은 문제가 다시 쌓인다.

### 지침의 영역 보존

책임 경계, 명시성, 질문 기준 같은 판단 규칙을 스크립트로 강제하려 하면
false positive가 늘고 검사가 지침을 왜곡한다.
판단의 영역은 md에 남긴다.

---

## 트레이드오프

### 스크립트 유지 비용

구조가 바뀌면 검사도 함께 갱신해야 한다.

### false positive 가능

검사가 과하면 정당한 변경을 막는다.
검사 실패가 잘못이면 우회하지 말고 검사를 고친다.

---

## 현재 판단

강제와 지침의 분리는 base 하네스 자체뿐 아니라 프로젝트에도 같은 원칙으로 적용한다.

프로젝트에서 기계 판정 가능한 컨벤션(테스트 패키지 구조, 커밋 형식 등)은
CI 또는 훅으로 강제하는 것을 기본 입장으로 한다.

---

## 재검토 신호

- 검사 실패의 다수가 false positive다.
- 스크립트가 검사하는 규칙과 문서 지침이 어긋난다.
- 훅을 우회(--no-verify)하는 커밋이 반복된다.
