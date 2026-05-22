package roomescape;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationWaitingAcceptanceTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @Test
    void 예약_취소시_첫번째_대기가_예약으로_승격되고_남은_대기_순번이_당겨진다() {
        long themeId = createTheme();
        long timeId = createTime("10:00");
        String date = LocalDate.now().plusDays(1).toString();
        createReservation("브라운", date, timeId, themeId);
        createWaiting("포비", date, timeId, themeId)
                .body("sequence", is(1));
        createWaiting("라이언", date, timeId, themeId)
                .body("sequence", is(2));

        RestAssured.given().log().all()
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(200);

        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", equalTo("포비"));

        RestAssured.given().log().all()
                .queryParam("date", date)
                .queryParam("timeId", timeId)
                .queryParam("themeId", themeId)
                .when().get("/reservation-waitings")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].name", equalTo("라이언"))
                .body("[0].sequence", is(1));
    }

    @Test
    void 예약되지_않은_일정에는_대기할_수_없다() {
        long themeId = createTheme();
        long timeId = createTime("10:00");
        String date = LocalDate.now().plusDays(1).toString();

        createWaiting("포비", date, timeId, themeId)
                .statusCode(400)
                .body("code", equalTo("WAITING_NOT_AVAILABLE"));
    }

    private long createTheme() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "잠실 미스터리");
        params.put("description", "설명");
        params.put("thumbnailUrl", "https://example.com/theme.jpg");

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

    private void createReservation(String name, String date, long timeId, long themeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("date", date);
        params.put("timeId", timeId);
        params.put("themeId", themeId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(200);
    }

    private io.restassured.response.ValidatableResponse createWaiting(
            String name,
            String date,
            long timeId,
            long themeId
    ) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("date", date);
        params.put("timeId", timeId);
        params.put("themeId", themeId);

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservation-waitings")
                .then().log().all();
    }
}
