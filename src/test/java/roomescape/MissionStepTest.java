package roomescape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import roomescape.reservation.presentation.ReservationController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MissionStepTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReservationController reservationController;

    @TestConfiguration
    static class FixedClockConfig {

        @Bean
        @Primary
        Clock fixedClock() {
            return Clock.fixed(Instant.parse("2026-05-21T00:00:00Z"), ZoneId.systemDefault());
        }
    }

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @Test
    void 예약_조회() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 예약_추가_및_삭제() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "브라운");
        params.put("date", "2023-08-05");
        params.put("time", "15:40");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("id", is(1));

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 데이터베이스_연동() {
        try (Connection connection = jdbcTemplate.getDataSource().getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.getCatalog()).isEqualTo("DATABASE");
            assertThat(connection.getMetaData().getTables(null, null, "RESERVATION", null).next()).isTrue();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void DB_조회_API_전환() {
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time) VALUES (?, ?, ?)",
                "브라운",
                "2023-08-05",
                "15:40"
        );

        List<Map<String, Object>> reservations = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".");

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);

        assertThat(reservations.size()).isEqualTo(count);
    }

    @Test
    void 시간_관리_API() {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 테마_관리_API() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "잠실 미스터리");
        params.put("description", "사라진 열쇠를 찾는 테마");
        params.put("thumbnailUrl", "https://example.com/theme.jpg");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(200)
                .body("id", is(1));

        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .when().delete("/themes/1")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 예약_가능_시간_조회_예약_생성_다시_조회하면_빠짐() {
        long themeId = createTheme();
        long ten = createTime("10:00");
        createTime("11:00");
        String date = testToday().plusDays(1).toString();

        RestAssured.given().log().all()
                .queryParam("date", date)
                .queryParam("themeId", themeId)
                .when().get("/available-times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", "브라운");
        reservation.put("date", date);
        reservation.put("timeId", ten);
        reservation.put("themeId", themeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("time.id", is((int) ten))
                .body("theme.id", is((int) themeId));

        RestAssured.given().log().all()
                .queryParam("date", date)
                .queryParam("themeId", themeId)
                .when().get("/available-times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].id", is(2));
    }

    @Test
    void 같은_날짜_시간이라도_테마가_다르면_예약할_수_있다() {
        long firstThemeId = createTheme();
        long secondThemeId = createTheme("북촌의 밤");
        long timeId = createTime("10:00");
        String date = testToday().plusDays(1).toString();

        createReservation("브라운", date, timeId, firstThemeId)
                .log().all()
                .statusCode(200);

        createReservation("포비", date, timeId, secondThemeId)
                .log().all()
                .statusCode(200);

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) FROM reservation", Integer.class);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void 중복_예약과_지난_예약은_거부한다() {
        long themeId = createTheme();
        long timeId = createTime("10:00");
        String futureDate = testToday().plusDays(1).toString();

        createReservation("브라운", futureDate, timeId, themeId)
                .log().all()
                .statusCode(200);

        createReservation("포비", futureDate, timeId, themeId)
                .log().all()
                .statusCode(409)
                .body("code", equalTo("RESERVATION_DUPLICATE"));

        createReservation("라이언", testToday().minusDays(1).toString(), timeId, themeId)
                .log().all()
                .statusCode(400)
                .body("code", equalTo("RESERVATION_PAST"));
    }

    @Test
    void 예약이_존재하는_시간은_삭제할_수_없다() {
        long themeId = createTheme();
        long timeId = createTime("10:00");
        createReservation("브라운", testToday().plusDays(1).toString(), timeId, themeId)
                .log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().delete("/times/" + timeId)
                .then().log().all()
                .statusCode(409)
                .body("code", equalTo("TIME_IN_USE"));
    }

    @Test
    void 내_예약_조회_변경_취소() {
        long themeId = createTheme();
        long firstTimeId = createTime("10:00");
        long secondTimeId = createTime("11:00");
        String firstDate = testToday().plusDays(1).toString();
        String secondDate = testToday().plusDays(2).toString();
        createReservation("브라운", firstDate, firstTimeId, themeId)
                .log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .queryParam("name", "브라운")
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        Map<String, Object> change = new HashMap<>();
        change.put("name", "브라운");
        change.put("date", secondDate);
        change.put("timeId", secondTimeId);
        change.put("themeId", themeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(change)
                .when().patch("/reservations/1")
                .then().log().all()
                .statusCode(200)
                .body("date", equalTo(secondDate))
                .body("time.id", is((int) secondTimeId));

        RestAssured.given().log().all()
                .queryParam("name", "브라운")
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(200);

        Integer count = jdbcTemplate.queryForObject("SELECT count(1) FROM reservation", Integer.class);
        assertThat(count).isZero();
    }

    @Test
    void 계층화_리팩터링() {
        boolean isJdbcTemplateInjected = false;

        for (Field field : reservationController.getClass().getDeclaredFields()) {
            if (field.getType().equals(JdbcTemplate.class)) {
                isJdbcTemplateInjected = true;
                break;
            }
        }

        assertThat(isJdbcTemplateInjected).isFalse();
    }

    @Test
    @Sql("/data.sql")
    void 인기_테마_조회() {
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3))
                .body("[0].id", is(1))
                .body("[0].reservationCount", is(4));
    }

    private long createTheme() {
        return createTheme("잠실 미스터리");
    }

    private LocalDate testToday() {
        return LocalDate.parse("2026-05-21");
    }

    private long createTheme(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("description", name + " 설명");
        params.put("thumbnailUrl", "https://example.com/" + name + ".jpg");

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private long createTime(String startAt) {
        Map<String, String> params = new HashMap<>();
        params.put("startAt", startAt);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getLong("id");
    }

    private io.restassured.response.ValidatableResponse createReservation(
            String name,
            String date,
            long timeId,
            long themeId
    ) {
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("name", name);
        reservation.put("date", date);
        reservation.put("timeId", timeId);
        reservation.put("themeId", themeId);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservation)
                .when().post("/reservations")
                .then();
    }
}
