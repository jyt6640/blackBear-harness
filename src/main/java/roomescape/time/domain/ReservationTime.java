package roomescape.time.domain;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import roomescape.global.exception.BadRequestException;

public class ReservationTime {

    private final Long id;
    private final String startAt;

    private ReservationTime(Long id, String startAt) {
        validateStartAt(startAt);
        this.id = id;
        this.startAt = startAt;
    }

    public static ReservationTime create(String startAt) {
        return new ReservationTime(null, startAt);
    }

    public static ReservationTime restore(Long id, String startAt) {
        return new ReservationTime(id, startAt);
    }

    public Long id() {
        return id;
    }

    public String startAt() {
        return startAt;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ReservationTime reservationTime)) {
            return false;
        }
        return id != null && Objects.equals(id, reservationTime.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }
        return Objects.hash(id);
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
