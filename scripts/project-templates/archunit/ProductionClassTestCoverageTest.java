package __ROOT_PACKAGE__;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * "모든 production class는 직접 테스트한다"(testing.md) 강제.
 *
 * <p>표준 ArchUnit 규칙으로는 "대응 테스트 존재"를 검사할 수 없어,
 * main 클래스를 스캔해 같은 패키지에 {@code <이름>Test} 가 있는지 확인한다.
 * 없으면 빌드를 깬다. = production class 직접 테스트가 green-bar의 일부가 된다.
 *
 * <p>제외 대상은 testing.md:직접 테스트 예외 가능 대상을 그대로 따른다.
 * 프로젝트가 제외를 늘리려면 EXCLUDED_SUFFIXES / isExcluded 를 조정한다.
 *
 * → docs/decisions/accepted/architecture-rules-as-archunit.md
 */
class ProductionClassTestCoverageTest {

    private static final String ROOT_PACKAGE = "__ROOT_PACKAGE__";

    // testing.md: 직접 테스트 예외 가능 대상 (단순 DTO / 설정 / 부트스트랩 / 상수형)
    private static final String[] EXCLUDED_SUFFIXES = {
            "Request", "Response", "Command", "Query",
            "Config", "Configuration", "Application", "ErrorCode"
    };

    @Test
    @DisplayName("모든 production class는 직접 테스트한다")
    void every_production_class_has_direct_test() {
        JavaClasses mainClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(ROOT_PACKAGE);

        JavaClasses testClasses = new ClassFileImporter()
                .importPackages(ROOT_PACKAGE);

        List<String> missing = new ArrayList<>();
        for (JavaClass production : mainClasses) {
            if (isExcluded(production)) {
                continue;
            }
            String expectedTest = production.getFullName() + "Test";
            boolean hasTest = testClasses.stream()
                    .anyMatch(c -> c.getFullName().equals(expectedTest));
            if (!hasTest) {
                missing.add(production.getFullName());
            }
        }

        assertThat(missing)
                .withFailMessage(
                        "직접 테스트가 없는 production class:%n%s%n"
                                + "각 클래스에 대응 <이름>Test를 작성하거나, 제외 사유가 있으면 "
                                + "isExcluded에 근거와 함께 반영하라.",
                        String.join("\n", missing))
                .isEmpty();
    }

    private boolean isExcluded(JavaClass production) {
        if (production.isInterface() || production.isEnum()
                || production.isAnnotation() || production.isRecord()) {
            return true;
        }
        if (production.getSimpleName().isEmpty()) { // 익명/합성
            return true;
        }
        for (String suffix : EXCLUDED_SUFFIXES) {
            if (production.getSimpleName().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
