---
name: harness-sync
description: base 하네스 버전이 갱신됐을 때 프로젝트 하네스와의 차이를 재검토한다. base 하네스가 업데이트됐다고 할 때, 프로젝트 AGENTS.md의 base 선언 버전과 실제 base 버전이 다를 때 사용한다.
---

# Harness Sync 실행

이 스킬은 얇은 런처다. 절차의 정본은 문서에 있고, 여기에 절차를 복사하지 않는다.

1. 정본 문서를 읽는다.
   - [docs/workflow/how-to-create-project-harness.md](../../../docs/workflow/how-to-create-project-harness.md)의 "base 갱신 시" 섹션
   - base 하네스 저장소 밖에서 실행 중이면, 프로젝트 AGENTS.md의 base 선언 경로에서 위 문서를 찾는다.

2. 프로젝트 AGENTS.md의 base 선언 버전과 현재 base 버전을 비교한다.

3. 그 사이 변경된 base decision 목록을 만든다. (git log 또는 decisions 디렉토리 diff)

4. 각 변경을 프로젝트 decision과 대조한다.
   - 프로젝트 override와 충돌하면 프로젝트가 우선하되,
     base의 변경 이유를 검토해 유지 여부를 사람에게 묻는다.
   - base에 새 pending이 생겼으면 해당 영역 질문으로 부분 재진입한다.
   - base에 새 accepted가 생겼고 프로젝트에 관련 결정이 없으면 기본 적용을 보고한다.

5. 검토 결과를 보고하고, 사람 확인 후 프로젝트 AGENTS.md의 base 선언 버전을 갱신한다.
