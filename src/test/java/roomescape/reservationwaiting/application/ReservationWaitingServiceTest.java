package roomescape.reservationwaiting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.reservationwaiting.application.dto.ReservationWaitingCreateCommand;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingErrorCode;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;
import roomescape.reservationwaiting.domain.fake.FakeReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.fake.FakeThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.fake.FakeReservationTimeRepository;

class ReservationWaitingServiceTest {

    @Test
    void create_success() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.markReservedSlotExists();
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        ReservationWaitingService service = service(reservationRepository, waitingRepository);

        ReservationWaiting waiting = service.create(command("브라운"));

        assertThat(waiting.sequence()).isEqualTo(1);
        assertThat(waitingRepository.waitings()).hasSize(1);
    }

    @Test
    void create_success_with_next_sequence() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.markReservedSlotExists();
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        waitingRepository.save(waiting("포비", 1));
        ReservationWaitingService service = service(reservationRepository, waitingRepository);

        ReservationWaiting waiting = service.create(command("브라운"));

        assertThat(waiting.sequence()).isEqualTo(2);
    }

    @Test
    void create_fail_when_reservation_slot_is_not_reserved() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        ReservationWaitingService service = service(reservationRepository, waitingRepository);

        assertThatThrownBy(() -> service.create(command("브라운")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationWaitingErrorCode.WAITING_NOT_AVAILABLE);
    }

    @Test
    void create_fail_when_same_name_already_waiting() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.markReservedSlotExists();
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        waitingRepository.save(waiting("브라운", 1));
        ReservationWaitingService service = service(reservationRepository, waitingRepository);

        assertThatThrownBy(() -> service.create(command("브라운")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationWaitingErrorCode.WAITING_DUPLICATE);
    }

    private ReservationWaitingService service(
            ReservationRepository reservationRepository,
            ReservationWaitingRepository waitingRepository
    ) {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        timeRepository.save(ReservationTime.create("10:00"));
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        themeRepository.save(Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg"));
        return new ReservationWaitingService(
                waitingRepository,
                reservationRepository,
                timeRepository,
                themeRepository,
                new ReservationWaitingValidator(waitingRepository, reservationRepository)
        );
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
