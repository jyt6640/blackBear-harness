package roomescape.reservationwaiting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

class ReservationWaitingTest {

    @Test
    void create_success() {
        ReservationWaiting waiting = ReservationWaiting.create(
                "브라운",
                "2026-05-22",
                reservationTime(),
                theme(),
                1
        );

        assertThat(waiting.sequence()).isEqualTo(1);
    }

    @Test
    void create_fail_with_invalid_sequence() {
        assertThatThrownBy(() -> ReservationWaiting.create(
                "브라운",
                "2026-05-22",
                reservationTime(),
                theme(),
                0
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toReservation_success() {
        ReservationWaiting waiting = ReservationWaiting.create(
                "브라운",
                "2026-05-22",
                reservationTime(),
                theme(),
                1
        );

        Reservation reservation = waiting.toReservation();

        assertThat(reservation.name()).isEqualTo("브라운");
        assertThat(reservation.date()).isEqualTo("2026-05-22");
        assertThat(reservation.time()).isEqualTo(reservationTime());
        assertThat(reservation.theme()).isEqualTo(theme());
    }

    @Test
    void advanceSequence_success() {
        ReservationWaiting waiting = new ReservationWaiting(
                1L,
                "브라운",
                "2026-05-22",
                reservationTime(),
                theme(),
                2
        );

        ReservationWaiting advanced = waiting.advanceSequence();

        assertThat(advanced.id()).isEqualTo(1L);
        assertThat(advanced.sequence()).isEqualTo(1);
    }

    private ReservationTime reservationTime() {
        return new ReservationTime(1L, "10:00");
    }

    private Theme theme() {
        return new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }
}
