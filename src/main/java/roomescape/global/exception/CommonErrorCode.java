package roomescape.global.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

    INVALID_REQUEST("COMMON_INVALID_REQUEST", HttpStatus.BAD_REQUEST, "요청 본문을 확인해 주세요."),
    INVALID_PARAMETER("COMMON_INVALID_PARAMETER", HttpStatus.BAD_REQUEST, "요청 파라미터를 확인해 주세요.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    CommonErrorCode(String code, HttpStatus status, String message) {
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
