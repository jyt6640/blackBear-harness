package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.BadRequestException;
import roomescape.global.exception.BusinessException;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

class   ReservationTest {

    @Test
    void create_success() {
        Reservation reservation = Reservation.create("브라운", "2026-05-22", time(), theme());

        assertThat(reservation.name()).isEqualTo("브라운");
        assertThat(reservation.hasStructuredSchedule()).isTrue();
        assertThat(reservation.displayTime()).isEqualTo("10:00");
    }

    @Test
    void legacy_success() {
        Reservation reservation = Reservation.legacy("브라운", "2026-05-22", "10:00");

        assertThat(reservation.hasStructuredSchedule()).isFalse();
        assertThat(reservation.displayTime()).isEqualTo("10:00");
    }

    @Test
    void create_fail_with_blank_name() {
        assertThatThrownBy(() -> Reservation.create(" ", "2026-05-22", time(), theme()))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.INVALID_RESERVATION);
    }

    @Test
    void create_fail_with_invalid_date() {
        assertThatThrownBy(() -> Reservation.create("브라운", "2026/05/22", time(), theme()))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.INVALID_RESERVATION);
    }

    @Test
    void validateReservable_success_when_future_schedule() {
        Reservation reservation = Reservation.create("브라운", "2026-05-22", time(), theme());

        reservation.validateReservable(LocalDateTime.parse("2026-05-21T09:00:00"));
    }

    @Test
    void validateReservable_fail_with_past_schedule() {
        Reservation reservation = Reservation.create("브라운", "2026-05-22", time(), theme());

        assertThatThrownBy(() -> reservation.validateReservable(LocalDateTime.parse("2026-05-22T10:00:00")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.PAST_RESERVATION);
    }

    @Test
    void validateCancelable_fail_with_past_schedule() {
        Reservation reservation = Reservation.create("브라운", "2026-05-22", time(), theme());

        assertThatThrownBy(() -> reservation.validateCancelable(LocalDateTime.parse("2026-05-22T10:00:00")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.PAST_CANCEL);
    }

    @Test
    void validateOwner_fail_with_other_name() {
        Reservation reservation = Reservation.create("브라운", "2026-05-22", time(), theme());

        assertThatThrownBy(() -> reservation.validateOwner("포비"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.NOT_OWNER);
    }

    @Test
    void changeSchedule_success() {
        Reservation reservation = new Reservation(1L, "브라운", "2026-05-22", null, time(), theme());
        ReservationTime changedTime = new ReservationTime(2L, "12:00");

        Reservation changed = reservation.changeSchedule("2026-05-23", changedTime, theme());

        assertThat(changed.id()).isEqualTo(1L);
        assertThat(changed.date()).isEqualTo("2026-05-23");
        assertThat(changed.time()).isEqualTo(changedTime);
    }

    private ReservationTime time() {
        return new ReservationTime(1L, "10:00");
    }

    private Theme theme() {
        return new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }
}
