# Lombok Usage Guideline

## 상태

accepted

---

## 문제 상황

Lombok annotation을 허용 목록 / 금지 목록으로 기계적으로 정할지,
객체 역할에 따라 판단할지 결정이 필요했다.

---

## 선택한 방향

Lombok은 객체 역할과 책임을 기준으로 사용한다.

annotation 이름만으로 기계적으로 허용하거나 금지하지 않는다.

---

## 선택 이유

### Lombok은 도구다

Lombok은 보일러플레이트를 줄이는 도구다.

하지만 생성 경로와 상태 변경 권한을 외부에 열면
도메인 객체의 책임이 약해진다.

---

### 역할별 기준이 필요하다

Domain 객체와 Persistence Entity는 같은 레이어에 있을 수 있지만 역할이 다르다.

- Domain 객체: 도메인 규칙과 행위
- Persistence Entity / row DTO: 테이블 row 저장 표현
- DTO: 외부 요청/응답 또는 레이어 간 전달

따라서 Lombok 사용 기준도 역할에 따라 달라진다.

---

## 현재 판단

Domain 객체에서는 생성 경로와 상태 변경 통제가 우선이다.

- `@Getter`는 사용할 수 있다.
- `@Setter`, `@Data`는 사용하지 않는다.
- `@Builder`는 생성 규칙을 우회할 수 있어 조심한다.

Persistence Entity / row DTO에서는 `@Data`, 생성자 annotation, `@Builder`가 가능할 수 있다.

Domain + Entity 겸임 객체에서는 Domain 책임이 우선한다.
따라서 public 생성자, setter, 생성 규칙을 우회하는 builder를 피한다.

---

## 트레이드오프

### 명시적 코드 증가

Domain 객체에서 Lombok 사용을 제한하면 생성자, equals/hashCode 등을 직접 작성해야 할 수 있다.

---

### 역할 판단 필요

객체가 Domain 객체인지 Persistence Entity인지 DTO인지 먼저 판단해야 한다.

프로젝트 규모와 도메인 복잡도에 따라 Domain 객체와 Persistence Entity 분리 여부를 확인한다.

---

## 재검토 신호

- Lombok annotation 때문에 생성 규칙이 우회된다.
- setter로 상태 변경이 외부에 열린다.
- id 기반 동등성이 Lombok 자동 생성으로 깨진다.
- DTO와 Domain 객체의 기준이 섞인다.
