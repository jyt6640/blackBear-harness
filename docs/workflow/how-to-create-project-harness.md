# How To Create Project Harness

이 문서는 base 하네스를 기반으로 프로젝트 / 회사 하네스를 만드는 절차를 정의한다.

프로젝트 하네스는 base 하네스의 복사본이 아니라,
base와 다른 결정만 기록하는 diff 문서다.

---

## 적용 시점

이 문서는 프로젝트 하네스를 만들 때만 읽는다.

프로젝트 하네스가 이미 있으면
(base 선언이 있는 프로젝트 AGENTS.md와 decisions가 존재하면)
이 문서로 돌아오지 않고 프로젝트 하네스를 따른다.

다시 읽는 조건:

- base 하네스 버전이 갱신되어 프로젝트 하네스와의 차이를 재검토할 때
- 프로젝트 규모가 커져 상위 프로파일의 질문이 필요해질 때
- not-applicable로 기록한 영역의 재검토 신호가 나타날 때

---

## 핵심 방향

- base 하네스의 불변 철학과 기본 입장은 다시 쓰지 않고 참조한다.
- 프로젝트 하네스에는 base와 다른 것만 둔다.
- base가 비워둔 슬롯(상위 전제, pending)을 채우는 것이 핵심 작업이다.
- 확정되지 않은 항목은 문서에 박지 않고 질문으로 남긴다.

---

## 프로젝트 하네스에 들어가는 것

1. base 선언 — 어떤 base 하네스의 어떤 버전을 기반으로 하는지 기록한다.
2. 상위 전제의 답
3. pending 영역에 대한 프로젝트 decision (해당 없으면 not-applicable로 기록)
4. 기본 입장 override — base와 다르게 정한 경우에만 기록한다.
5. 프로젝트 사실 — 기술 스택, 루트 패키지, 도메인 용어, 운영 인프라, 커밋/브랜치 규칙 차이
6. development-guideline.md — 팀원이 바로 따를 개발 규칙. 각 규칙은 근거 decision을 링크한다.
7. verify.sh — 프로젝트 green-bar(build + test). `scripts/project-templates/verify.sh`를 복사해 채운다. → [project-verify-green-bar](../decisions/accepted/project-verify-green-bar.md)

## 들어가면 안 되는 것

- base 내용의 복사 — 같은 규칙이 두 곳에 존재하면 드리프트가 생긴다.
- 확정되지 않은 논의 — QnA 초안에 둔다.
- 코드에서 파생 가능한 사실 — 코드가 정본이다.

---

## 채워야 하는 슬롯

### 상위 전제

base의 [AGENTS.md](../../AGENTS.md) 작업 시퀀스가 확인을 요구하는 항목이다.

- 저장소 기술 고정 여부 — 기술 예외 전파와 Repository 예외 계약 수준을 함께 정한다 → [spring-dao-exception-propagation](../decisions/pending/spring-dao-exception-propagation.md), [repository-exception-contract](../decisions/pending/repository-exception-contract.md)
- Domain / Persistence Entity 분리 여부 → [domain-entity-separation](../decisions/pending/domain-entity-separation.md)
- 트랜잭션 성공 기준과 후속 작업 실패 경계 → [follow-up-failure-boundary](../decisions/accepted/follow-up-failure-boundary.md)

### pending 결정

base의 [docs/decisions/pending](../decisions/pending) 디렉토리가 최소 체크리스트다.
전체 질문 목록과 규모별 깊이는 [harness-interview.md](./harness-interview.md)의 결정 영역을 따른다.

여기에 목록을 복제하지 않는다. pending이 늘어나면 디렉토리와 인터뷰만 갱신한다.

### 프로젝트 사실

- 예상 사용자 규모와 트래픽
- 기술 스택과 영속성 기술
- 루트 패키지 이름
- 도메인 목록과 용어
- 후속 작업 재시도 인프라 (스케줄러 / 큐 / 상태 테이블 / 수동 보상)
- 외부 연동과 운영 전제

---

## 생성 경로 1: 신규 프로젝트

코드가 없으므로 답은 사람에게서 나온다.

인터뷰 절차와 규모별 질문 세트는 [harness-interview.md](./harness-interview.md)를 따른다.

1. 위 슬롯 목록으로 기획 / 설계 담당과 인터뷰한다.
2. 답을 프로젝트 decisions/accepted로 기록한다. 이유와 트레이드오프를 함께 남긴다.
3. 답이 나오지 않은 항목은 프로젝트 pending으로 두고, 해당 영역 작업 시 구현 전에 질문한다.

---

## 생성 경로 2: 기존 베이스 코드

코드가 있으므로 먼저 관찰하고, 사람에게 확인받는다.

확인 질문 방식은 [harness-interview.md](./harness-interview.md)의 3단계를 따른다.

1. production code, 문서, git 기록을 관찰한다.
2. 슬롯별로 관찰된 전제를 draft decision으로 작성한다. 관찰 근거(파일, 코드 위치)를 함께 남긴다.
3. 사람과 확인한 뒤 accepted로 승격한다.
4. 코드에서 답이 나오지 않거나 코드끼리 일관되지 않은 항목은 임의로 정하지 않고 질문한다.

작업자가 관찰만으로 accepted를 만들지 않는다.
관찰은 제안이고, 결정은 사람과의 확인을 거친다.

---

## base 갱신 시

- 프로젝트 하네스는 기반 base 버전을 기록하고 있어야 한다.
- base가 갱신되면 변경된 decision 목록을 확인한다.
- 프로젝트 override와 충돌하면 프로젝트 decision이 우선하되, base의 변경 이유를 검토한 뒤 유지 여부를 다시 판단한다.

---

## 판단 기준

프로젝트 하네스에 무언가를 쓸지 헷갈리면 아래 질문으로 판단한다.

- 이 내용이 base에 이미 있는가? → 있으면 쓰지 않는다.
- 프로젝트가 바뀌면 달라지는 내용인가? → 아니라면 base로 보낼 불변 철학 후보다.
- 확정된 합의인가? → 아니라면 decision이 아니라 QnA 초안이다.
