package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationErrorCode;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

class ReservationValidatorTest {

    @Test
    void validateCreatable_success() {
        ReservationValidator validator = new ReservationValidator(new FakeReservationRepository());

        validator.validateCreatable(reservation(null));
    }

    @Test
    void validateCreatable_fail_with_duplicate_schedule() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.save(reservation(null));
        ReservationValidator validator = new ReservationValidator(reservationRepository);

        assertThatThrownBy(() -> validator.validateCreatable(reservation(null)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.DUPLICATE_RESERVATION);
    }

    @Test
    void validateChangeable_success_when_same_reservation() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.save(reservation(null));
        ReservationValidator validator = new ReservationValidator(reservationRepository);

        validator.validateChangeable(reservation(1L));
    }

    @Test
    void validateChangeable_fail_with_other_reservation_schedule() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.save(reservation(null));
        ReservationValidator validator = new ReservationValidator(reservationRepository);

        assertThatThrownBy(() -> validator.validateChangeable(reservation(2L)))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationErrorCode.DUPLICATE_RESERVATION);
    }

    private Reservation reservation(Long id) {
        return Reservation.restore(id, "브라운", "2026-05-22", null, time(), theme());
    }

    private ReservationTime time() {
        return ReservationTime.restore(1L, "10:00");
    }

    private Theme theme() {
        return Theme.restore(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

}
