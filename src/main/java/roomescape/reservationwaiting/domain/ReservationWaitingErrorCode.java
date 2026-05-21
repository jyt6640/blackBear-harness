package roomescape.reservationwaiting.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ReservationWaitingErrorCode implements ErrorCode {

    WAITING_NOT_AVAILABLE("WAITING_NOT_AVAILABLE", HttpStatus.BAD_REQUEST, "예약된 시간에만 대기할 수 있습니다."),
    WAITING_DUPLICATE("WAITING_DUPLICATE", HttpStatus.CONFLICT, "이미 같은 예약에 대기 중입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ReservationWaitingErrorCode(String code, HttpStatus status, String message) {
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
