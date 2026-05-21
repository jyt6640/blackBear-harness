package roomescape.global.exception;

public class BusinessException extends RoomEscapeException {

    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
