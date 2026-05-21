# 방탈출 예약 미션

## 기능 목록

- [x] 관리자 예약 CRUD API
- [x] H2 + JdbcTemplate 기반 예약 저장
- [x] 예약 시간 추가/조회/삭제 API
- [x] 예약과 예약 시간 연결
- [x] Controller / Service / Repository / Domain 계층 분리
- [x] 테마 추가/조회/삭제 API
- [x] 날짜와 테마 기준 예약 가능 시간 조회
- [x] 사용자 예약 생성
- [x] 최근 1주 인기 테마 상위 10개 조회
- [x] 서비스 정책 기반 에러 처리
- [x] 내 예약 조회/변경/취소
- [x] 사용자 화면에서 정상 흐름과 에러 메시지 확인
- [x] 회원가입 API
- [x] JWT 기반 로그인 API
- [x] JWT로 내 회원 정보 조회
- [ ] 예약 대기 등록 API
- [ ] 예약 대기 조회 API
- [ ] 예약 취소 시 첫 번째 대기 자동 예약 승격

## API 명세

### 인증

| 기능 | 메서드 / URL | 요청 | 응답 |
| --- | --- | --- | --- |
| 회원가입 | `POST /members` | `{name, email, password}` | `{id, name, email}` |
| 로그인 | `POST /login` | `{email, password}` | `{accessToken, tokenType}` |
| 내 정보 조회 | `GET /members/me` | `Authorization: Bearer {token}` | `{id, name, email}` |

### 예약

| 기능 | 메서드 / URL | 요청 | 응답 |
| --- | --- | --- | --- |
| 전체 예약 조회 | `GET /reservations` | - | `[{id, name, date, time, theme}]` |
| 예약 생성 | `POST /reservations` | `{name, date, timeId, themeId}` | `{id, name, date, time, theme}` |
| 관리자 예약 삭제 | `DELETE /reservations/{id}` | - | `200 OK` |
| 내 예약 조회 | `GET /reservations/mine?name=브라운` | - | `[{id, name, date, time, theme}]` |
| 내 예약 변경 | `PATCH /reservations/{id}` | `{name, date, timeId, themeId}` | `{id, name, date, time, theme}` |
| 내 예약 취소 | `DELETE /reservations/{id}?name=브라운` | - | `200 OK` |
| 예약 가능 시간 조회 | `GET /available-times?date=2026-05-22&themeId=1` | - | `[{id, startAt}]` |

### 예약 대기

| 기능 | 메서드 / URL | 요청 | 응답 |
| --- | --- | --- | --- |
| 예약 대기 등록 | `POST /reservation-waitings` | `{name, date, timeId, themeId}` | `{id, name, date, time, theme, sequence}` |
| 예약 대기 조회 | `GET /reservation-waitings?date=2026-05-22&timeId=1&themeId=1` | - | `[{id, name, date, time, theme, sequence}]` |

예약 대기는 이미 예약된 날짜+시간+테마에만 등록할 수 있다.
예약이 취소되면 같은 날짜+시간+테마의 가장 빠른 대기가 예약으로 승격되고, 나머지 대기 순번은 앞으로 당겨진다.

초기 단계 테스트 호환을 위해 `POST /reservations`는 `{name, date, time}` 형식도 받을 수 있다.

### 시간

| 기능 | 메서드 / URL | 요청 | 응답 |
| --- | --- | --- | --- |
| 시간 조회 | `GET /times` | - | `[{id, startAt}]` |
| 시간 추가 | `POST /times` | `{startAt}` | `{id, startAt}` |
| 시간 삭제 | `DELETE /times/{id}` | - | `200 OK` |

### 테마

| 기능 | 메서드 / URL | 요청 | 응답 |
| --- | --- | --- | --- |
| 테마 조회 | `GET /themes` | - | `[{id, name, description, thumbnailUrl}]` |
| 테마 추가 | `POST /themes` | `{name, description, thumbnailUrl}` | `{id, name, description, thumbnailUrl}` |
| 테마 삭제 | `DELETE /themes/{id}` | - | `200 OK` |
| 인기 테마 조회 | `GET /themes/popular` | - | `[{id, name, description, thumbnailUrl, reservationCount}]` |

## 에러 응답 명세

공통 wrapper는 사용하지 않고, 실패 응답에만 명확한 에러 본문을 둔다.

```json
{
  "code": "RESERVATION_DUPLICATE",
  "message": "이미 예약된 시간입니다."
}
```

| 상황 | 상태 코드 | 대표 code |
| --- | --- | --- |
| 잘못된 입력 | `400` | `RESERVATION_INVALID`, `TIME_INVALID_START_AT`, `THEME_INVALID` |
| 지난 일정 예약/변경/취소 | `400` | `RESERVATION_PAST`, `RESERVATION_PAST_CANCEL` |
| 존재하지 않는 리소스 | `404` | `RESERVATION_NOT_FOUND`, `TIME_NOT_FOUND`, `THEME_NOT_FOUND` |
| 중복 예약 | `409` | `RESERVATION_DUPLICATE` |
| 대기 불가능한 예약 슬롯 | `400` | `WAITING_NOT_AVAILABLE` |
| 중복 대기 | `409` | `WAITING_DUPLICATE` |
| 예약이 존재하는 시간/테마 삭제 | `409` | `TIME_IN_USE`, `THEME_IN_USE` |
| 본인 예약이 아님 | `403` | `RESERVATION_NOT_OWNER` |
| 중복 이메일 | `409` | `MEMBER_DUPLICATE_EMAIL` |
| 로그인 실패 | `401` | `AUTH_LOGIN_FAILED` |
| 인증 토큰 없음/오류 | `401` | `AUTH_REQUIRED`, `AUTH_INVALID_TOKEN` |

## API 설계 결정과 이유

- 사용자 예약 생성은 관리자 예약 생성과 같은 `POST /reservations`를 사용했다. 예약이라는 같은 리소스를 만들기 때문에 URL을 나누기보다 요청 본문의 `timeId`, `themeId`로 새 사용자 흐름을 표현했다.
- 내 예약 조회는 로그인 없이 이름으로 식별한다는 요구사항에 맞춰 `GET /reservations/mine?name=...`로 정했다. 실제 인증이 생기면 `mine`은 인증 주체 기준으로 바꾸기 쉽다.
- 예약 가능 시간은 예약 자체가 아니라 선택 보조 목록이므로 `GET /available-times`로 분리했다. 날짜와 테마가 필터 조건이라 query parameter로 표현했다.
- 에러 응답은 `code`, `message`만 둔다. 사용자는 메시지로 다음 행동을 이해하고, 클라이언트는 code로 분기할 수 있다.
- `data.sql`의 인기 테마 검증 데이터는 테스트 리소스에 두고 `@Sql`로 필요한 테스트에서만 사용한다. 기본 미션 단계의 빈 DB 기대와 충돌하지 않게 하기 위한 선택이다.
- 로그인은 JWT access token을 발급하고, 인증이 필요한 API는 `Authorization: Bearer` 헤더를 사용한다. 서버 세션을 만들지 않아 API 클라이언트와 브라우저 화면이 같은 방식으로 인증을 처리할 수 있기 때문이다.
- 회원 테이블은 `member_account`로 이름 붙였다. `user` 같은 기술/DB 예약어 충돌 가능성이 있는 표현보다 현재 도메인의 회원 계정을 명시하기 위해서다.
- 예약 대기는 별도 리소스 `reservation-waitings`로 분리한다. 대기는 예약과 상태가 다르고, 취소 시 예약으로 승격되는 후보 목록이라는 독립된 생명주기를 가지기 때문이다.

## 미션 중 기록

### 규칙을 적용해서 변경한 코드

- `ReservationController`가 `JdbcTemplate`을 알지 않도록 `ReservationService`와 `ReservationRepository`로 분리했다.
- 저장 기술은 `reservation/domain/ReservationRepository` 인터페이스와 `reservation/infrastructure/JdbcReservationRepository` 구현체로 나누어 Domain이 JDBC를 모르게 했다.
- 공통 응답 wrapper는 만들지 않고, 성공 응답은 리소스 DTO를 직접 반환했다. rejected decision인 generic response wrapper를 따르지 않은 결정이다.

### 테스트 작성이 어려웠던 코드

- 인기 테마 조회는 “오늘 기준 최근 1주”라는 시간 의존성이 있다. 현재는 `Clock`을 Bean으로 분리해 서비스가 직접 시스템 시간을 만들지 않게 했고, 테스트 데이터는 `src/test/resources/data.sql`을 `@Sql`로 주입했다.
- 초기 단계 테스트는 빈 DB를 기대하지만 인기 테마 검증은 충분한 초기 데이터가 필요하다. 그래서 자동 로딩 데이터가 아니라 필요한 테스트에서만 주입하는 방식으로 충돌을 줄였다.

### 막힌 순간

- 3단계 이후 스키마는 `time_id`를 요구하지만 1, 2단계 테스트는 `time` 문자열 컬럼을 직접 사용한다. 최신 구조를 유지하면서도 이전 단계 테스트를 통과시키기 위해 `reservation.time`을 호환 컬럼으로 남기고, 새 응답에서는 `timeId` 기반 예약만 `{id, startAt}` 객체로 반환하도록 했다.

### 변경/취소에서 발견한 엣지 케이스와 처리 방향

- 이미 지난 예약을 사용자가 취소하거나 변경하면 `400 RESERVATION_PAST_CANCEL`로 거부한다.
- 변경하려는 날짜+시간+테마가 이미 차 있으면 `409 RESERVATION_DUPLICATE`로 응답한다.
- 이름이 다른 사용자가 변경/취소를 시도하면 `403 RESERVATION_NOT_OWNER`를 반환한다.
- 관리자가 예약이 걸린 시간을 삭제하면 기존 예약이 깨지므로 `409 TIME_IN_USE`로 거부한다.

### 유지/수정/폐기한 규칙

- 유지: 도메인 우선 패키지 구조, Repository 인터페이스의 Domain 배치, Service orchestration only.
- 수정: 초기 단계 호환을 위해 예약 생성 정책을 둘로 나눴다. `time` 문자열 기반 관리자 호환 요청은 과거 날짜도 허용하고, `timeId + themeId` 기반 사용자 예약에는 서비스 정책을 적용한다.
- 폐기: 단순 CRUD라는 이유로 Controller에 DB 접근을 두는 방식은 폐기했다.

## 실행

```bash
gradle bootRun
```

브라우저에서 `http://localhost:8080`으로 접속하면 사용자 예약 화면을 확인할 수 있다.

## 검증

```bash
gradle test
```
