package roomescape.reservationwaiting.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.reservationwaiting.application.dto.ReservationWaitingCreateCommand;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingErrorCode;
import roomescape.reservationwaiting.domain.fake.FakeReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

class ReservationWaitingValidatorTest {

    @Test
    void validateCreatable_success() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.markReservedSlotExists();
        ReservationWaitingValidator validator = new ReservationWaitingValidator(
                new FakeReservationWaitingRepository(),
                reservationRepository
        );

        validator.validateCreatable(command("브라운"));
    }

    @Test
    void validateCreatable_fail_when_reservation_slot_is_not_reserved() {
        ReservationWaitingValidator validator = new ReservationWaitingValidator(
                new FakeReservationWaitingRepository(),
                new FakeReservationRepository()
        );

        assertThatThrownBy(() -> validator.validateCreatable(command("브라운")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationWaitingErrorCode.WAITING_NOT_AVAILABLE);
    }

    @Test
    void validateCreatable_fail_when_same_name_already_waiting() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.markReservedSlotExists();
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        waitingRepository.save(waiting("브라운", 1));
        ReservationWaitingValidator validator = new ReservationWaitingValidator(waitingRepository, reservationRepository);

        assertThatThrownBy(() -> validator.validateCreatable(command("브라운")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationWaitingErrorCode.WAITING_DUPLICATE);
    }

    private ReservationWaitingCreateCommand command(String name) {
        return new ReservationWaitingCreateCommand(name, "2026-05-22", 1L, 1L);
    }

    private ReservationWaiting waiting(String name, int sequence) {
        return ReservationWaiting.restore(1L, name, "2026-05-22", time(), theme(), sequence);
    }

    private ReservationTime time() {
        return ReservationTime.restore(1L, "10:00");
    }

    private Theme theme() {
        return Theme.restore(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

}
