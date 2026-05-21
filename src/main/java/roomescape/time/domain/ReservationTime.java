package roomescape.time.domain;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import roomescape.global.exception.BadRequestException;

public record ReservationTime(Long id, String startAt) {

    public ReservationTime {
        validateStartAt(startAt);
    }

    public static ReservationTime create(String startAt) {
        return new ReservationTime(null, startAt);
    }

    private static void validateStartAt(String startAt) {
        if (startAt == null || startAt.isBlank()) {
            throw new BadRequestException(ReservationTimeErrorCode.INVALID_START_AT);
        }
        try {
            LocalTime.parse(startAt);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException(ReservationTimeErrorCode.INVALID_START_AT);
        }
    }
}
