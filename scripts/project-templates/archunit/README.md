# ArchUnit 근간 철학 강제 템플릿

`docs/architecture` + `docs/principles`의 **기계 판정 가능한 구조 규칙(Tier 1)**을
ArchUnit 테스트로 강제하는 정본 템플릿이다. 프로젝트가 복사해 패키지명을 채우면,
이 규칙을 어기는 코드는 `verify.sh` green-bar를 통과할 수 없다. = "절대 못 어김".

→ 근거: [architecture-rules-as-archunit](../../../docs/decisions/accepted/architecture-rules-as-archunit.md)

## 들어 있는 것

| 파일 | 역할 |
|---|---|
| `ArchitectureTest.java` | 의존방향·Domain순수성·생성/상태·트랜잭션/예외·패키지/네이밍 규칙 |
| `ProductionClassTestCoverageTest.java` | 모든 production class 직접 테스트 강제 |
| `AggregateRoot.java` | Entity/Aggregate 식별 마커 (생성자 private 규칙 대상) |

## 적용 절차

1. 의존성 추가 (Gradle 예시):
   ```
   testImplementation 'com.tngtech.archunit:archunit-junit5:1.3.0'
   ```
2. 세 파일을 프로젝트로 복사한다.
   - `ArchitectureTest.java`, `ProductionClassTestCoverageTest.java` → `src/test/java/<루트>/`
   - `AggregateRoot.java` → `src/main/java/<루트>/global/support/`
3. 모든 `__ROOT_PACKAGE__`를 프로젝트 루트 패키지로 치환한다.
4. 패키지 관례(`domain`/`application`/`presentation`/`infrastructure`/`global`)가
   다르면 규칙 안의 패키지 식별자를 조정한다.
5. Entity/Aggregate 클래스에 `@AggregateRoot`를 붙인다.
6. `verify.sh`가 test를 포함하는지 확인한다 (green-bar에 포함되어야 강제가 발효).

## 규칙을 끄려면

프로젝트 decision으로 특정 규칙을 끄려면 **해당 `@ArchTest` 필드를 지운다.**
base 파일을 건드릴 필요가 없다. (grep 훅과 달리 우선순위 역전이 없다.)

## 경계

- **Tier 1만** 여기 둔다. 의미 판단(Service에 비즈니스 판단, Tell-Don't-Ask,
  최소 변경)은 Review + 점수표(Tier 3)의 몫이다.
- 스타일(삼항/else/메서드 길이)은 여기 두지 않는다. 린터 WARN(Tier 2)이다.
- `scripts/check-philosophy.sh`의 구조 검사(H1/H3/H5/H6 등)는 ArchUnit이 정확히
  대체한다. Java 빌드 전 빠른 1차 차단으로 grep을 남길 수는 있으나, 정본 판정은
  ArchUnit green-bar다.
