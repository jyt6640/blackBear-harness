package roomescape.reservationwaiting.domain;

import java.util.Objects;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public class ReservationWaiting {

    private final Long id;
    private final String name;
    private final String date;
    private final ReservationTime time;
    private final Theme theme;
    private final int sequence;

    private ReservationWaiting(Long id, String name, String date, ReservationTime time, Theme theme, int sequence) {
        if (sequence < 1) {
            throw new IllegalArgumentException("대기 순번은 1 이상이어야 합니다.");
        }
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.sequence = sequence;
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

    public static ReservationWaiting restore(
            Long id,
            String name,
            String date,
            ReservationTime time,
            Theme theme,
            int sequence
    ) {
        return new ReservationWaiting(id, name, date, time, theme, sequence);
    }

    public Reservation toReservation() {
        return Reservation.create(name, date, time, theme);
    }

    public ReservationWaiting advanceSequence() {
        return new ReservationWaiting(id, name, date, time, theme, sequence - 1);
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

    public ReservationTime time() {
        return time;
    }

    public Theme theme() {
        return theme;
    }

    public int sequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ReservationWaiting waiting)) {
            return false;
        }
        return id != null && Objects.equals(id, waiting.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }
        return Objects.hash(id);
    }
}
