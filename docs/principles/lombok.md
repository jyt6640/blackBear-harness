# Lombok

이 문서는 Lombok 사용 기준을 정의한다.

Lombok은 보일러플레이트 코드를 줄이는 도구다.
하지만 객체의 생성 경로와 상태 변경 권한을 외부에 열어서는 안 된다.

---

## 핵심 방향

- annotation 목록으로 기계적으로 허용하거나 금지하지 않는다.
- 객체의 역할과 책임을 흐리는지로 판단한다.
- Domain 객체에서는 도메인 행위, 생성 의도, 상태 변경 통제가 Lombok 편의성보다 우선한다.
- Domain 객체와 Persistence Entity의 역할 차이를 구분한다.

---

## 역할별 기준

### Domain 객체

Domain 객체는 도메인 규칙과 행위를 가진다.

- `@Getter`는 사용할 수 있다.
- `@Setter`와 `@Data`는 사용하지 않는다.
- 상태 변경은 명시적인 도메인 행위로 표현한다.
- 생성 의도는 정적 팩터리 메서드로 표현한다.

필드를 그대로 노출하면 도메인 의도가 흐려지는 경우에는 getter를 직접 작성하거나 의도 있는 메서드로 제공한다.

### Persistence Entity / Row DTO

Persistence Entity는 테이블 row와 1대1 매핑되는 저장 표현에 가깝다.

- `@Data`, 생성자 annotation, `@Builder`가 가능할 수 있다.
- 도메인 규칙과 행위를 가진 객체와 역할이 다르다.

### Domain + Entity 겸임 객체

프로젝트 규모와 복잡도에 따라 하나의 객체가 Domain 객체와 Entity 역할을 겸할 수 있다.
분리 여부는 프로젝트 규모와 도메인 복잡도를 확인해 결정한다.

겸임 객체에서는 Domain 책임이 우선한다.

- `@Setter`, `@Data`를 사용하지 않는다.
- public 생성자를 열지 않는다.
- 생성 규칙을 우회하는 `@Builder`를 피한다.
- 필요한 경우 `@RequiredArgsConstructor(access = AccessLevel.PRIVATE)`로 생성 경로를 닫고 정적 팩터리만 연다.

### DTO / Test Fixture

Request / Response DTO, 테스트 fixture, 복잡한 테스트 데이터 생성에서는 `@Builder` 등 생성 편의 annotation을 사용할 수 있다.

---

## Annotation 기준

### `@Getter`

Domain 객체에서도 사용할 수 있다.

다만 getter 존재가 외부 비즈니스 판단 허용을 뜻하지 않는다.
Service가 getter로 값을 꺼내 도메인 규칙을 판단하기 시작하면 Domain 행위나 Policy로 옮긴다.

### `@Setter`

Domain 객체와 Domain + Entity 겸임 객체에는 사용하지 않는다.

상태 변경 권한을 외부에 열고,
도메인 행위를 데이터 변경으로 약화시킬 수 있기 때문이다.

### `@Data`

Domain 객체에는 사용하지 않는다.

`@Data`는 setter, equals/hashCode, toString 등을 함께 열어 Domain 책임과 동등성 기준을 흐릴 수 있다.
Persistence Entity / row DTO에서는 가능할 수 있다.

### `@Builder`

DTO, 테스트 fixture, Persistence Entity / row DTO에서는 가능할 수 있다.

Domain 객체와 Domain + Entity 겸임 객체에서는 생성 규칙을 우회할 수 있으므로 조심한다.

### 생성자 annotation

`@NoArgsConstructor`는 프레임워크 매핑 요구가 있는 Persistence Entity / row DTO에서는 허용 가능하다.
Domain 객체에는 기본적으로 맞지 않는다.

`@AllArgsConstructor`는 Domain 객체의 생성 의도를 흐릴 수 있어 피한다.
필요하다면 `access = PRIVATE`로 제한하고 static factory 내부 생성 구현에만 사용한다.

`@RequiredArgsConstructor(access = AccessLevel.PRIVATE)`는 Domain + Entity 겸임 객체에서 생성 경로를 닫고 static factory만 열 때 적절하다.

### `@EqualsAndHashCode`

Value Object는 값 기반 동등성이 자연스러우므로 사용할 수 있다.

Domain + Entity 겸임 객체는 id 기반 동등성이 필요할 수 있으므로 직접 작성하는 편이 낫다.

### `@ToString`

로그 편의는 있지만 민감정보, 연관 객체 순환, 과한 출력 위험이 있으므로 Domain에서는 조심한다.
DTO나 테스트 객체에서는 허용 가능하다.

---

## 판단 기준

Lombok 사용이 헷갈리면 아래 질문으로 판단한다.

- 생성 경로가 외부에 열리는가?
- 상태 변경 권한이 외부에 열리는가?
- 도메인 행위보다 필드 접근이 먼저 보이는가?
- 동등성 기준이 객체 역할과 맞는가?
- 이 객체는 Domain 객체인가, Persistence Entity인가, DTO인가?

하나라도 위험하다면 annotation보다 명시적인 코드를 우선한다.
