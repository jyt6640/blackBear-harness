package roomescape.global.exception;

public class EntityNotFoundException extends RoomEscapeException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
