# Mechanical Philosophy Block

## 상태

accepted

---

## 문제 상황

약한 로컬 LLM이 철학을 자주 어겼다. 점수표 루프가 이를 잡지만
위반이 Review 단계에서야 드러나, 약한 모델이 구현을 다 한 뒤 반려되어
시간이 낭비됐다.

위반 중 상당수는 코드 의미 판단이 필요 없는 '명백한' 것이었다.
이런 위반은 사람이나 강모델이 아니라 grep으로 판정할 수 있다.

---

## 선택한 방향

기계 판정 가능한 철학 위반은 `scripts/check-philosophy.sh`로 사전 차단한다.

Feat / Refactor 단계 게이트(enforce-workflow)가 이 검사를 실행하고,
위반이 있으면 단계가 실패한다. 약한 모델은 리뷰 전에 스스로 고치게 된다.

차단(BLOCK) 대상:

- 책임 불명 클래스명 (Util / Helper / Manager)
- Controller의 try-catch (예외는 global 핸들러로)
- Domain의 setter
- 클래스 내부 중첩 클래스
- global 밖의 예외 핸들러 (@ControllerAdvice / @ExceptionHandler)
- dto 패키지 밖의 DTO (Request / Response / Command / Query)
- throw new RuntimeException
- 삼항 연산자 (return / 대입의 ?:)

검토 신호(WARN, 차단 안 함):

- else 블록 (early return / throw로 풀 수 있는지)

---

## 선택 이유

### 약한 모델에 가장 효과적

명백한 위반이 게이트에서 막히면 약한 모델이 즉시 재시도한다.
점수표(사후)보다 빠르고, 강모델 승격(비쌈)보다 싸다.

### 기계가 잡을 건 기계가 잡는다

코드 의미 판단(Service에 비즈니스 판단, 트랜잭션 경계 등)은
여전히 점수표 루프와 Review의 몫이다.
grep으로 판정 가능한 것만 사전 차단해 LLM 판단 부담을 줄인다.
→ [enforcement-by-script](./enforcement-by-script.md)

---

## 트레이드오프

### 오탐 / 미탐

grep 휴리스틱은 프로젝트 패키지 규칙에 따라 오탐이 날 수 있다.
경로 관례(domain / dto / global, *Controller)를 전제하며,
관례가 다른 프로젝트는 패턴을 조정한다.
의미가 필요한 위반은 잡지 못한다(미탐) — 그건 점수표가 잡는다.

### 정당한 예외 차단 위험

삼항 / else처럼 정당한 사용이 있는 패턴은 과하게 막을 수 있다.
else는 WARN(차단 안 함)으로 두고, 삼항은 프로젝트가 허용하면 패턴을 끈다.

---

## 현재 판단

기계 판정 가능한 명백한 위반만 사전 차단한다.
패턴 목록은 실제로 약한 모델이 자주 어기는 것에서 출발했고,
점수표에서 반복 감점되는 항목 중 기계화 가능한 것이 나오면 추가한다.

---

## 재검토 신호

- 오탐으로 정당한 코드가 반복 차단된다 (패턴 조정).
- 점수표에서 특정 항목이 계속 감점되는데 기계화 가능하다 (검사 추가).
- 프로젝트 패키지 관례가 전제와 달라 검사가 무의미하다.
