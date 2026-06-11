# Decisions

이 디렉토리는 이 하네스의 기본 입장과 그 고민 과정을 기록한다.

결정은 고정된 정답이 아니라,
적용 대상 프로젝트에 결정이 없을 때 출발점이 되는 기본 입장이다.

프로젝트 / 회사 하네스는 자기 decision으로 기본 입장을 덮어쓸 수 있다.
덮어쓸 때는 해당 문서의 트레이드오프와 재검토 신호를 함께 검토한다.

---

## 목적

- 왜 이 입장을 기본으로 선택했는지 기록한다.
- 다시 같은 고민을 반복하지 않도록 한다.
- 선택 당시의 트레이드오프를 남긴다.
- 프로젝트마다 달라질 수 있는 지점과 재검토 신호를 남긴다.

---

## 디렉토리 구조

    decisions/
    ├── accepted/
    ├── rejected/
    └── pending/

---

## accepted

이 하네스가 기본 입장으로 채택한 결정이다.
적용 대상 프로젝트의 accepted decision이 있으면 그쪽이 우선한다.

예시:

- [accepted/domain-first-package-structure.md](./accepted/domain-first-package-structure.md)
- [accepted/service-orchestration-only.md](./accepted/service-orchestration-only.md)
- [accepted/repository-interface-in-domain.md](./accepted/repository-interface-in-domain.md)

---

## rejected

검토했지만 기본적으로 피하는 방향으로 정리한 결정이다.

예시:

- [rejected/anemic-domain-model.md](./rejected/anemic-domain-model.md)
- [rejected/common-util-package.md](./rejected/common-util-package.md)
- [rejected/service-layer-business-logic.md](./rejected/service-layer-business-logic.md)

---

## pending

하네스가 기본 입장을 정하지 않은 영역이다.
프로젝트마다 결정이 필요하며, 작업자는 결정이 없으면 임의로 정하지 않고 구현 전에 질문한다.

예시:

- [pending/domain-entity-separation.md](./pending/domain-entity-separation.md)
- [pending/security-auth-pattern.md](./pending/security-auth-pattern.md)
- [pending/event-driven-boundary.md](./pending/event-driven-boundary.md)

---

## 기록 기준

아래 상황이면 decision 문서를 추가한다.

- 같은 고민이 반복된다.
- 선택지가 2개 이상 존재한다.
- 트레이드오프가 명확하다.
- 나중에 다시 검토할 가능성이 높다.
- 코드 구조에 큰 영향을 준다.

---

## 문서 형식

각 decision 문서는 아래 구조를 따른다.

1. 상태
2. 문제 상황
3. 선택한 방향
4. 선택 이유
5. 트레이드오프
6. 현재 판단
7. 재검토 신호

---

## 상태 기준

### accepted

하네스의 기본 입장으로 채택한 결정이다.

### rejected

하네스 기준으로 기본적으로 피하는 방향이다.

### pending

하네스가 기본 입장을 정하지 않고 프로젝트 결정에 맡긴 영역이다.

---

## 핵심 원칙

문서는 코드를 고정하기 위한 규칙이 아니다.

적용 대상 프로젝트의 코드와 이 문서가 충돌하면:

1. 코드가 잘못된 것인지 확인한다.
2. 문서가 현재 코드와 맞지 않는지 확인한다.
3. 필요하면 문서를 수정한다.
4. 필요하면 구조를 수정한다.

결정은 경험을 통해 계속 갱신한다.

---

## 적용 강도

- accepted: 프로젝트 decision이 없으면 기본 적용한다. 프로젝트 decision이 있으면 그쪽이 우선한다.
- rejected: 프로젝트 decision이 따로 없으면 선택하지 않는 방향으로 본다.
- pending: 기본 입장 없음. 해당 영역의 결정이 필요하면 임의로 정하지 않고 질문한다.
- pending 문서는 accepted decision과 충돌할 수 없으며, 충돌처럼 보이면 accepted를 우선한다.