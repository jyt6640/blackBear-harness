package roomescape.time.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ReservationTimeErrorCode implements ErrorCode {

    TIME_NOT_FOUND("TIME_NOT_FOUND", HttpStatus.NOT_FOUND, "예약 시간을 찾을 수 없습니다."),
    TIME_IN_USE("TIME_IN_USE", HttpStatus.CONFLICT, "예약이 존재하는 시간은 삭제할 수 없습니다."),
    INVALID_START_AT("TIME_INVALID_START_AT", HttpStatus.BAD_REQUEST, "예약 시간 형식을 확인해 주세요.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ReservationTimeErrorCode(String code, HttpStatus status, String message) {
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
