package roomescape.global.exception;

public class BadRequestException extends RoomEscapeException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
