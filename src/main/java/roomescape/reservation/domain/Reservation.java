package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import roomescape.global.exception.BadRequestException;
import roomescape.global.exception.BusinessException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public class Reservation {

    private final Long id;
    private final String name;
    private final String date;
    private final String legacyTime;
    private final ReservationTime time;
    private final Theme theme;

    private Reservation(Long id, String name, String date, String legacyTime, ReservationTime time, Theme theme) {
        validateDate(date);
        if (legacyTime != null) {
            validateTime(legacyTime);
        }
        this.id = id;
        this.name = name;
        this.date = date;
        this.legacyTime = legacyTime;
        this.time = time;
        this.theme = theme;
    }

    public static Reservation legacy(String name, String date, String time) {
        return new Reservation(null, name, date, time, null, null);
    }

    public static Reservation create(String name, String date, ReservationTime time, Theme theme) {
        return new Reservation(null, name, date, null, time, theme);
    }

    public static Reservation restore(
            Long id,
            String name,
            String date,
            String legacyTime,
            ReservationTime time,
            Theme theme
    ) {
        return new Reservation(id, name, date, legacyTime, time, theme);
    }

    public Reservation changeSchedule(String date, ReservationTime time, Theme theme) {
        return new Reservation(id, name, date, legacyTime, time, theme);
    }

    public void validateReservable(LocalDateTime now) {
        if (!hasStructuredSchedule()) {
            return;
        }
        if (schedule().isBefore(now) || schedule().isEqual(now)) {
            throw new BusinessException(ReservationErrorCode.PAST_RESERVATION);
        }
    }

    public void validateCancelable(LocalDateTime now) {
        if (!hasStructuredSchedule()) {
            return;
        }
        if (schedule().isBefore(now) || schedule().isEqual(now)) {
            throw new BusinessException(ReservationErrorCode.PAST_CANCEL);
        }
    }

    public void validateOwner(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            return;
        }
        if (!name.equals(ownerName)) {
            throw new BusinessException(ReservationErrorCode.NOT_OWNER);
        }
    }

    public boolean hasStructuredSchedule() {
        return time != null && theme != null;
    }

    public String displayTime() {
        if (time != null) {
            return time.startAt();
        }
        return legacyTime;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String date() {
        return date;
    }

    public String legacyTime() {
        return legacyTime;
    }

    public ReservationTime time() {
        return time;
    }

    public Theme theme() {
        return theme;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Reservation reservation)) {
            return false;
        }
        return id != null && Objects.equals(id, reservation.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }
        return Objects.hash(id);
    }

    private LocalDateTime schedule() {
        return LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time.startAt()));
    }

    private static void validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException(ReservationErrorCode.INVALID_RESERVATION);
        }
    }

    private static void validateTime(String time) {
        try {
            LocalTime.parse(time);
        } catch (DateTimeParseException exception) {
            throw new BadRequestException(ReservationErrorCode.INVALID_RESERVATION);
        }
    }
}
