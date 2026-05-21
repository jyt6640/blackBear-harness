package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import roomescape.global.exception.BadRequestException;
import roomescape.global.exception.BusinessException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public record Reservation(
        Long id,
        String name,
        String date,
        String legacyTime,
        ReservationTime time,
        Theme theme
) {

    public Reservation {
        validateName(name);
        validateDate(date);
        if (legacyTime != null && !legacyTime.isBlank()) {
            validateTime(legacyTime);
        }
    }

    public static Reservation legacy(String name, String date, String time) {
        return new Reservation(null, name, date, time, null, null);
    }

    public static Reservation create(String name, String date, ReservationTime time, Theme theme) {
        return new Reservation(null, name, date, null, time, theme);
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

    private LocalDateTime schedule() {
        return LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time.startAt()));
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException(ReservationErrorCode.INVALID_RESERVATION);
        }
    }

    private static void validateDate(String date) {
        if (date == null || date.isBlank()) {
            throw new BadRequestException(ReservationErrorCode.INVALID_RESERVATION);
        }
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
