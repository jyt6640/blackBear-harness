# Next Step

`next-step`은 오케스트레이터와 역할 에이전트가 기능 개발 중 공유하는 단기 작업 메모리 패키지다.

---

## 구조

    next-step/
    ├── templates/
    │   ├── 00-task-card.md
    │   ├── 01-test-report.md
    │   ├── 02-implementation-report.md
    │   ├── 02-refactor-report.md
    │   ├── 03-review-report.md
    │   └── 04-summary.md
    └── work/
        ├── backlog.md      (카드 2장 이상일 때: 카드 목록·순서·진행 상태)
        └── <작업명>/

`templates/`는 커밋되는 정본이다.
`work/`는 작업 중에만 사용하는 임시 산출물 위치이며 커밋하지 않는다.

---

## 사용 흐름

1. 카드가 2장 이상 필요한 요청이면 오케스트레이터(/orchestrate)가 `templates/backlog.md`를 복사해 `work/backlog.md`를 먼저 작성한다.
2. 오케스트레이터가 `templates/00-task-card.md`를 복사해 `work/<작업명>/00-task-card.md`를 작성하고 멈춘다. 카드는 백로그 순번대로 한 장씩 컴파일한다.
3. Test Agent(/test-agent)는 `00-task-card.md`를 확인하고 `01-test-report.md`를 작성하고 멈춘다.
4. Feat Agent(/feat-agent)는 `01-test-report.md`를 확인하고 `02-implementation-report.md`를 작성하고 멈춘다.
5. Refactor Agent(/refactor-agent)는 구현을 정리하고 `02-refactor-report.md`를 작성하고 멈춘다. 개선할 것이 없어도 행위 보존 확인을 기록한다.
6. Review Agent(/review-agent)는 산출물과 diff를 검증해 `03-review-report.md`를 작성하고 멈춘다. 반려면 사유에 따라 Feat / Refactor / Test로 되돌린다.
7. 오케스트레이터는 `04-summary.md`를 작성하고 백로그 상태를 갱신한 뒤 영구화할 지식만 docs / decisions / 커밋 메시지 / PR 설명으로 승격한다.
8. 카드 완료 후 `work/<작업명>`은 삭제한다. 모든 카드가 끝나면 `backlog.md`도 삭제한다.

사용자 릴레이 모드에서는 각 단계를 사용자의 스킬 호출로 시작한다.
자동 로컬 에이전트 모드에서는 `scripts/local-agent/run-pipeline.sh`가 같은 산출물
경계와 게이트를 유지하며 역할별 로컬 에이전트를 직렬 실행한다.

각 역할은 자기 `agents/<role>/scripts/enforce-workflow.sh`를 먼저 실행해 필요한 산출물과 템플릿이 있는지 확인한다.
