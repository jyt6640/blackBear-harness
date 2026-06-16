# Next Step History

Review 승인과 `05-summary.md` 작성까지 끝난 작업의 산출물 체인을 보존한다.

## 보존 단위

```text
next-step/history/
├── <작업명>/
│   ├── 00-task-card.md
│   ├── 01-red-test-report.md
│   ├── 02-green-implementation-report.md
│   ├── 03-refactor-report.md
│   ├── 04-review-report.md
│   └── 05-summary.md
└── <백로그명>-backlog.md
```

## 규칙

- 작업 중 산출물은 `next-step/work/`에 두고 커밋하지 않는다.
- 승인된 완료 산출물만 history로 이동해 커밋한다.
- 같은 이름의 history를 덮어쓰지 않는다.
- history는 완료 당시 기록이므로 직접 수정하지 않는다.
- 반복 적용할 지식은 별도로 `docs/` 또는 decisions에 승격한다.
