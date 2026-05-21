package roomescape.auth.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    LOGIN_FAILED("AUTH_LOGIN_FAILED", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호를 확인해 주세요."),
    AUTH_REQUIRED("AUTH_REQUIRED", HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    INVALID_TOKEN("AUTH_INVALID_TOKEN", HttpStatus.UNAUTHORIZED, "인증 토큰을 확인해 주세요.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    AuthErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String message() {
        return message;
    }
}
