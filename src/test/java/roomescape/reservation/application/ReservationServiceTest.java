package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.fake.FakeReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.fake.FakeThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.fake.FakeReservationTimeRepository;

class ReservationServiceTest {

    @Test
    void delete_success_promotes_first_waiting() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.add(reservation(1L, "브라운"));
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        waitingRepository.add(waiting(1L, "포비", 1));
        waitingRepository.add(waiting(2L, "라이언", 2));
        ReservationService service = service(reservationRepository, waitingRepository);

        service.delete(1L, null);

        assertThat(reservationRepository.deletedIds()).containsExactly(1L);
        assertThat(reservationRepository.reservations()).extracting(Reservation::name)
                .containsExactly("포비");
        assertThat(waitingRepository.deletedIds()).containsExactly(1L);
        assertThat(waitingRepository.isAdvanced()).isTrue();
    }

    private ReservationService service(
            FakeReservationRepository reservationRepository,
            FakeReservationWaitingRepository waitingRepository
    ) {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        timeRepository.save(ReservationTime.create("10:00"));
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        themeRepository.save(Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg"));
        return new ReservationService(
                reservationRepository,
                timeRepository,
                themeRepository,
                new ReservationValidator(reservationRepository),
                waitingRepository,
                Clock.fixed(Instant.parse("2026-05-21T00:00:00Z"), ZoneId.systemDefault())
        );
    }

    private Reservation reservation(Long id, String name) {
        return Reservation.restore(id, name, "2026-05-22", null, time(), theme());
    }

    private ReservationWaiting waiting(Long id, String name, int sequence) {
        return ReservationWaiting.restore(id, name, "2026-05-22", time(), theme(), sequence);
    }

    private ReservationTime time() {
        return ReservationTime.restore(1L, "10:00");
    }

    private Theme theme() {
        return Theme.restore(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

}
