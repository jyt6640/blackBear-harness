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
        └── <작업명>/

`templates/`는 커밋되는 정본이다.
`work/`는 작업 중에만 사용하는 임시 산출물 위치이며 커밋하지 않는다.

---

## 사용 흐름

1. 오케스트레이터가 `templates/00-task-card.md`를 복사해 `work/<작업명>/00-task-card.md`를 작성한다.
2. Test Agent는 `00-task-card.md`를 확인하고 `01-test-report.md`를 작성한다.
3. Feat Agent는 `01-test-report.md`를 확인하고 `02-implementation-report.md`를 작성한다.
4. Review Agent는 `02-implementation-report.md`를 확인하고 `03-review-report.md`를 작성한다.
5. 오케스트레이터는 `04-summary.md`를 작성한 뒤 영구화할 지식만 docs / decisions / 커밋 메시지 / PR 설명으로 승격한다.
6. 기능 완료 후 `work/<작업명>`은 삭제한다.

각 역할은 자기 `agents/<role>/scripts/enforce-workflow.sh`를 먼저 실행해 필요한 산출물과 템플릿이 있는지 확인한다.
