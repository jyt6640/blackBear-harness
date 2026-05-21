package roomescape.global.exception;

public class RoomEscapeException extends RuntimeException {

    private final ErrorCode errorCode;

    public RoomEscapeException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }
}
