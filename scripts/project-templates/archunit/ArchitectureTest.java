package __ROOT_PACKAGE__;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * 근간 철학 Tier 1 강제.
 *
 * docs/architecture, docs/principles의 문장 중 "기계가 AST로 판정 가능한"
 * 구조 규칙만 여기에 둔다. verify.sh green-bar에 포함되므로, 이 규칙을 어기는
 * 코드는 빌드를 통과할 수 없다. = "절대 못 어김".
 *
 * 의미 판단이 필요한 규칙(Service에 비즈니스 판단 누적, Tell-Don't-Ask,
 * 최소 변경 등)은 여기 두지 않는다. 그건 Review + 점수표(Tier 3)의 몫이다.
 * 스타일(삼항/else/메서드 길이)은 여기 두지 않는다. 그건 린터 WARN(Tier 2)이다.
 *
 * 프로젝트 적용:
 *   1. __ROOT_PACKAGE__ 를 프로젝트 루트 패키지로 치환한다.
 *   2. @AnalyzeClasses(packages) 를 루트 패키지로 맞춘다.
 *   3. 패키지 관례(domain/application/presentation/infrastructure/global)가
 *      다르면 아래 패키지 식별자를 프로젝트 관례로 조정한다.
 *   4. AggregateRoot 마커 위치가 다르면 import를 고친다.
 *   5. 규칙을 프로젝트 decision으로 끄려면 해당 @ArchTest 필드를 지운다.
 *      base 파일을 건드릴 필요가 없다.
 *
 * → docs/decisions/accepted/architecture-rules-as-archunit.md
 */
@AnalyzeClasses(packages = "__ROOT_PACKAGE__")
class ArchitectureTest {

    // ── 그룹 1. 의존 방향 (layered-architecture.md) ──────────────────────
    // Presentation→Application→Domain. Infrastructure는 Domain 인터페이스 구현.
    // Controller의 Repository 직접 호출·레이어 스킵을 함께 차단한다.
    @ArchTest
    static final ArchRule 레이어_의존_방향 = layeredArchitecture().consideringAllDependencies()
            .layer("Presentation").definedBy("..presentation..")
            .layer("Application").definedBy("..application..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();

    // ── 그룹 2. Domain 순수성 (layered-architecture.md:Domain 금지) ────────
    // Domain은 HTTP/DB/Framework 어노테이션과 Infrastructure/Presentation에
    // 의존하지 않는다. 저장 기술과 무관한 순수 Java 객체로 유지한다.
    @ArchTest
    static final ArchRule 도메인은_기술을_모른다 = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "jakarta.persistence..",
                    "jakarta.servlet..",
                    "org.springframework..",
                    "..infrastructure..",
                    "..presentation..")
            .as("Domain은 기술(JPA/Servlet/Spring)·Infrastructure·Presentation에 의존하지 않는다");

    // ── 그룹 3. 생성/상태 통제 (oop.md, lombok.md, domain-boundary.md) ─────
    @ArchTest
    static final ArchRule 도메인에_setter_금지 = noMethods()
            .that().areDeclaredInClassesThat().resideInAPackage("..domain..")
            .should().haveNameMatching("set[A-Z].*")
            .as("Domain에 setter 금지 — 상태 변경은 명시적 도메인 행위로 표현한다");

    @ArchTest
    static final ArchRule 도메인에_setter_data_어노테이션_금지 = noClasses()
            .that().resideInAPackage("..domain..")
            .should().beAnnotatedWith("lombok.Setter")
            .orShould().beAnnotatedWith("lombok.Data")
            .as("Domain에 @Setter / @Data 금지 — 상태 변경 권한을 외부에 열지 않는다");

    // @AggregateRoot 마커가 붙은 Entity/Aggregate는 생성자를 닫고 정적 팩터리만 연다.
    @ArchTest
    static final ArchRule 애그리거트_생성자_private = constructors()
            .that().areDeclaredInClassesThat().areAnnotatedWith(
                    __ROOT_PACKAGE__.global.support.AggregateRoot.class)
            .should().bePrivate()
            .as("@AggregateRoot 생성자는 private — 생성은 정적 팩터리(create/restore)로만");

    // ── 그룹 4. 트랜잭션 / 예외 (transactions.md, exceptions.md) ───────────
    @ArchTest
    static final ArchRule 트랜잭션은_application에만 = classes()
            .that().areAnnotatedWith("org.springframework.transaction.annotation.Transactional")
            .should().resideInAPackage("..application..")
            .as("@Transactional은 Application Service에만 — Controller/Repository/Domain 금지");

    @ArchTest
    static final ArchRule 예외핸들러는_global에만 = classes()
            .that().areAnnotatedWith("org.springframework.web.bind.annotation.RestControllerAdvice")
            .or().areAnnotatedWith("org.springframework.web.bind.annotation.ControllerAdvice")
            .should().resideInAPackage("..global..")
            .as("@ControllerAdvice / @RestControllerAdvice는 global에만 둔다");

    @ArchTest
    static final ArchRule 커스텀_예외는_런타임_기반 = classes()
            .that().areAssignableTo(Throwable.class)
            .and().resideOutsideOfPackages("java..", "jakarta..", "org.springframework..")
            .should().beAssignableTo(RuntimeException.class)
            .as("커스텀 예외는 RuntimeException 기반(unchecked)으로 작성한다");

    // ── 그룹 5. 패키지 / 네이밍 (package-structure.md, naming.md) ──────────
    @ArchTest
    static final ArchRule request_response는_presentation_dto에 = classes()
            .that().haveSimpleNameEndingWith("Request").or().haveSimpleNameEndingWith("Response")
            .should().resideInAPackage("..presentation.dto..")
            .as("Request / Response DTO는 presentation/dto에 둔다");

    @ArchTest
    static final ArchRule command_query는_application_dto에 = classes()
            .that().haveSimpleNameEndingWith("Command").or().haveSimpleNameEndingWith("Query")
            .should().resideInAPackage("..application.dto..")
            .as("Command / Query DTO는 application/dto에 둔다");

    @ArchTest
    static final ArchRule repository_인터페이스는_domain에 = classes()
            .that().haveSimpleNameEndingWith("Repository").and().areInterfaces()
            .should().resideInAPackage("..domain..")
            .as("Repository 인터페이스는 Domain에 둔다 (구현은 Infrastructure)");

    @ArchTest
    static final ArchRule 책임불명_클래스명_금지 = noClasses()
            .should().haveSimpleNameEndingWith("Util")
            .orShould().haveSimpleNameEndingWith("Helper")
            .orShould().haveSimpleNameEndingWith("Manager")
            .as("Util / Helper / Manager 같은 책임 불명 이름을 쓰지 않는다");

    // 테스트 패키지 구조·production class 직접 테스트는 표준 ArchUnit으로 약하다.
    // → ProductionClassTestCoverageTest 가 별도로 강제한다.
}
