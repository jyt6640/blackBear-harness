package roomescape.time.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.fake.FakeReservationTimeRepository;

class ReservationTimeValidatorTest {

    @Test
    void validateRemovable_success() {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        timeRepository.save(ReservationTime.create("10:00"));
        ReservationTimeValidator validator = new ReservationTimeValidator(timeRepository, new FakeReservationRepository());

        validator.validateRemovable(1L);
    }

    @Test
    void validateRemovable_fail_with_not_found_time() {
        ReservationTimeValidator validator = new ReservationTimeValidator(
                new FakeReservationTimeRepository(),
                new FakeReservationRepository()
        );

        assertThatThrownBy(() -> validator.validateRemovable(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.TIME_NOT_FOUND);
    }

    @Test
    void validateRemovable_fail_with_time_in_use() {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        timeRepository.save(ReservationTime.create("10:00"));
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.markTimeInUse();
        ReservationTimeValidator validator = new ReservationTimeValidator(timeRepository, reservationRepository);

        assertThatThrownBy(() -> validator.validateRemovable(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.TIME_IN_USE);
    }

}
