# project-layout — 프로젝트 인스턴스 빈 틀

이 디렉토리는 **실제 Spring 백엔드 프로젝트에 복사될 빈 틀**이다.
base harness 저장소에는 실제 내용(`src/`, 실제 `generated/`, 실제 task-card)을 두지 않는다.

새 프로젝트 적용 절차는 [docs/HARNESS.md](../../docs/HARNESS.md)를 따른다.

## 구성

    AGENTS.md            프로젝트 진입점. base 선언 + 프로젝트 override만.
    ARCHITECTURE.md      이 프로젝트의 실제 구조 지도.
    verify               base scripts/verify를 호출하는 프로젝트 진입점.
    docs/
      exec-plans/
        active/          진행 중 작업 카드 (00~06 산출물). git 추적 정책은 프로젝트가 정함.
        completed/       완료 작업 보존.
      generated/         코드에서 생성하는 사실 (db-schema·api-spec·dependency-map). 손으로 쓰지 않음.
      decisions/         이 프로젝트가 base 기본 입장을 override한 결정.

## 채우는 것 / 덮어쓰지 않는 것

- 채운다: `generated/`, `exec-plans/`, 프로젝트 `decisions/`, `ARCHITECTURE.md`.
- 덮어쓰지 않는다: base의 `docs/principles`, `docs/architecture`, `agents/`, `templates/`, `scripts/`.
  base 규칙을 바꾸려면 프로젝트 `decisions/`에 override 결정을 쓴다.
