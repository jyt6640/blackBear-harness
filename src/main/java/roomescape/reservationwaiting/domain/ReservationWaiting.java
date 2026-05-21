package roomescape.reservationwaiting.domain;

import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public record ReservationWaiting(
        Long id,
        String name,
        String date,
        ReservationTime time,
        Theme theme,
        int sequence
) {

    public ReservationWaiting {
        if (sequence < 1) {
            throw new IllegalArgumentException("대기 순번은 1 이상이어야 합니다.");
        }
    }

    public static ReservationWaiting create(
            String name,
            String date,
            ReservationTime time,
            Theme theme,
            int sequence
    ) {
        return new ReservationWaiting(null, name, date, time, theme, sequence);
    }

    public Reservation toReservation() {
        return Reservation.create(name, date, time, theme);
    }

    public ReservationWaiting advanceSequence() {
        return new ReservationWaiting(id, name, date, time, theme, sequence - 1);
    }
}
