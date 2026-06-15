package __ROOT_PACKAGE__.global.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Domain Entity / Aggregate Root 식별 마커.
 *
 * <p>이 마커가 붙은 타입은 생성 경로를 정적 팩터리 메서드로만 열고
 * 생성자를 닫는다. ArchUnit(ArchitectureTest)이 이 마커로 Entity/Aggregate를
 * 식별해 생성자 private 규칙을 강제한다. → docs/decisions/accepted/architecture-rules-as-archunit.md
 *
 * <p>값 객체(Value Object), Command/Query, DTO에는 붙이지 않는다.
 * 이들은 생성자 정책이 다르므로 마커 대상이 아니다.
 *
 * <p>패키지 위치는 프로젝트 관례에 맞게 옮길 수 있다. 옮기면
 * ArchitectureTest의 import도 함께 고친다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AggregateRoot {
}
