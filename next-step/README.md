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
    ├── work/
        ├── backlog.md      (카드 2장 이상일 때: 카드 목록·순서·진행 상태)
        └── <작업명>/
    └── history/
        ├── <작업명>/       (승인과 04-summary까지 완료된 산출물)
        └── <백로그명>-backlog.md

`templates/`는 커밋되는 정본이다.
`work/`는 작업 중에만 사용하는 임시 산출물 위치이며 커밋하지 않는다.
`history/`는 완료된 작업의 산출물 체인을 보존하는 위치이며 커밋한다.

---

## 사용 흐름

1. 카드가 2장 이상 필요한 요청이면 오케스트레이터(/orchestrate)가 `templates/backlog.md`를 복사해 `work/backlog.md`를 먼저 작성한다.
2. 오케스트레이터가 `templates/00-task-card.md`를 복사해 `work/<작업명>/00-task-card.md`를 작성하고 멈춘다. 카드는 백로그 순번대로 한 장씩 컴파일한다.
3. Test Agent(/test-agent)는 `00-task-card.md`를 확인하고 `01-test-report.md`를 작성하고 멈춘다.
4. Feat Agent(/feat-agent)는 `01-test-report.md`를 확인하고 `02-implementation-report.md`를 작성하고 멈춘다.
5. Refactor Agent(/refactor-agent)는 구현을 정리하고 `02-refactor-report.md`를 작성하고 멈춘다. 개선할 것이 없어도 행위 보존 확인을 기록한다.
6. Review Agent(/review-agent)는 산출물과 diff를 검증해 `03-review-report.md`를 작성하고 멈춘다. 반려면 사유에 따라 Feat / Refactor / Test로 되돌린다.
7. 오케스트레이터는 `04-summary.md`를 작성하고 백로그 상태를 갱신한 뒤 영구화할 지식만 docs / decisions / 커밋 메시지 / PR 설명으로 승격한다.
8. 카드 완료 후 `work/<작업명>`을 `history/<작업명>/`으로 이동하고 완료 기록으로 커밋한다.
9. 모든 카드가 끝나면 `work/backlog.md`를 `history/<백로그명>-backlog.md`로 이동하고 함께 커밋한다.

## History 보존 규칙

- Review 판정이 승인이고 `04-summary.md`가 작성된 작업만 history로 이동한다.
- 같은 작업명의 history가 이미 있으면 덮어쓰지 않고 중단해 사람에게 확인한다.
- history로 이동한 산출물은 수정하지 않는다. 후속 작업은 새 작업 카드로 시작한다.
- `docs/`와 decisions에는 반복해서 적용할 지식만 승격하고, 실행 과정과 검증 기록은 history에 남긴다.

사용자 릴레이 모드에서는 각 단계를 사용자의 스킬 호출로 시작한다.
자동 로컬 에이전트 모드에서는 `scripts/local-agent/run-pipeline.sh`가 같은 산출물
경계와 게이트를 유지하며 역할별 로컬 에이전트를 직렬 실행한다.

각 역할은 자기 `agents/<role>/scripts/enforce-workflow.sh`를 먼저 실행해 필요한 산출물과 템플릿이 있는지 확인한다.
