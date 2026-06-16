# Code Style

이 문서는 가독성에 관한 코드 스타일 기준을 정의한다.

스타일은 철학(구조·책임)이 아니라 읽힘성의 문제다.
따라서 위반은 빌드를 깨는 강제(Tier 1)가 아니라 린터 WARN / 리뷰 신호로 다룬다.
구조 규칙은 [architecture](../architecture/index.md), 판단 규칙은 [principles](./index.md)을 따른다.

---

## 핵심 방향

- 코드는 한 번 쓰고 여러 번 읽힌다. 읽는 사람을 위해 쓴다.
- 타입은 import해서 짧은 이름으로 쓴다.
- 표현이 의미를 가리지 않게 한다.

---

## import 정책

- 타입은 import하고 본문에서는 단순 이름(simple name)으로 쓴다.
- 본문에 완전수식명(FQN, `com.c4.todo.schedule.domain.Visibility`)을 인라인으로 쓰지 않는다.

지양하는 예시:

    if (schedule.getVisibility() == com.c4.todo.schedule.domain.Visibility.PUBLIC)

좋은 예시:

    import com.c4.todo.schedule.domain.Visibility;
    ...
    if (visibility == Visibility.PUBLIC)

예외:

- 같은 simple name이 충돌해 한쪽을 FQN으로 써야 할 때만 허용한다. 이 경우도
  잦으면 네이밍 또는 패키지 구조를 먼저 의심한다.

강제 수준:

- `scripts/check-philosophy.sh`가 본문의 인라인 FQN을 WARN(차단 안 함)으로 표시한다.
  Tier 2 스타일이므로 빌드를 깨지 않는다. 프로젝트가 더 강하게 막으려면 checkstyle
  같은 린터 규칙으로 승격할 수 있다.

---

## 표현 정책

- enum 상수 비교가 본문에 반복되면 enum 행위 또는 다형성을 검토한다. → [method-design](./method-design.md)
- 매직 값(리터럴)은 의미 있는 상수로 분리한다.
- 한 줄에 의미를 과하게 욱여넣지 않는다.

---

## 판단 기준

스타일이 헷갈리면 아래로 판단한다.

- 이 줄을 처음 보는 사람이 한 번에 읽는가?
- 타입 경로가 의미를 가리지 않는가?
- 같은 표현이 반복되며 더 나은 추상을 요구하지 않는가?
