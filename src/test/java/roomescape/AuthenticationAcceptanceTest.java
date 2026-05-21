package roomescape;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticationAcceptanceTest {

    @Test
    void 회원가입_로그인_JWT로_내정보를_조회한다() {
        signup("브라운", "brown@example.com", "password1234");

        String accessToken = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest("brown@example.com", "password1234"))
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .body("tokenType", equalTo("Bearer"))
                .body("accessToken", notNullValue())
                .extract().jsonPath().getString("accessToken");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/members/me")
                .then().log().all()
                .statusCode(200)
                .body("name", equalTo("브라운"))
                .body("email", equalTo("brown@example.com"));
    }

    @Test
    void 중복_이메일로_회원가입할_수_없다() {
        signup("브라운", "brown@example.com", "password1234");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(signupRequest("포비", "brown@example.com", "password1234"))
                .when().post("/members")
                .then().log().all()
                .statusCode(409)
                .body("code", equalTo("MEMBER_DUPLICATE_EMAIL"));
    }

    @Test
    void 잘못된_비밀번호로_로그인할_수_없다() {
        signup("브라운", "brown@example.com", "password1234");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest("brown@example.com", "wrong-password"))
                .when().post("/login")
                .then().log().all()
                .statusCode(401)
                .body("code", equalTo("AUTH_LOGIN_FAILED"));
    }

    @Test
    void 토큰_없이_내정보를_조회할_수_없다() {
        RestAssured.given().log().all()
                .when().get("/members/me")
                .then().log().all()
                .statusCode(401)
                .body("code", equalTo("AUTH_REQUIRED"));
    }

    private void signup(String name, String email, String password) {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(signupRequest(name, email, password))
                .when().post("/members")
                .then().log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("email", equalTo(email));
    }

    private Map<String, String> signupRequest(String name, String email, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("name", name);
        request.put("email", email);
        request.put("password", password);
        return request;
    }

    private Map<String, String> loginRequest(String email, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", password);
        return request;
    }
}
