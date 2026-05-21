package roomescape.theme.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ThemeErrorCode implements ErrorCode {

    THEME_NOT_FOUND("THEME_NOT_FOUND", HttpStatus.NOT_FOUND, "테마를 찾을 수 없습니다."),
    THEME_IN_USE("THEME_IN_USE", HttpStatus.CONFLICT, "예약이 존재하는 테마는 삭제할 수 없습니다."),
    INVALID_THEME("THEME_INVALID", HttpStatus.BAD_REQUEST, "테마 정보를 확인해 주세요.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ThemeErrorCode(String code, HttpStatus status, String message) {
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
