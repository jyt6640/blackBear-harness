package roomescape.reservation.domain;

import org.springframework.http.HttpStatus;
import roomescape.global.exception.ErrorCode;

public enum ReservationErrorCode implements ErrorCode {

    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),
    INVALID_RESERVATION("RESERVATION_INVALID", HttpStatus.BAD_REQUEST, "예약 정보를 확인해 주세요."),
    PAST_RESERVATION("RESERVATION_PAST", HttpStatus.BAD_REQUEST, "지난 일정은 예약할 수 없습니다."),
    PAST_CANCEL("RESERVATION_PAST_CANCEL", HttpStatus.BAD_REQUEST, "지난 예약은 취소할 수 없습니다."),
    DUPLICATE_RESERVATION("RESERVATION_DUPLICATE", HttpStatus.CONFLICT, "이미 예약된 시간입니다."),
    NOT_OWNER("RESERVATION_NOT_OWNER", HttpStatus.FORBIDDEN, "본인의 예약만 변경하거나 취소할 수 있습니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ReservationErrorCode(String code, HttpStatus status, String message) {
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
