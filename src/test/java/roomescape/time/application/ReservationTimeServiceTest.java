package roomescape.time.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.fake.FakeReservationTimeRepository;

class ReservationTimeServiceTest {

    @Test
    void create_success() {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        ReservationTimeService service = service(timeRepository, new FakeReservationRepository());

        ReservationTime time = service.create("10:00");

        assertThat(time.id()).isEqualTo(1L);
        assertThat(timeRepository.findAll()).hasSize(1);
    }

    @Test
    void delete_success() {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        timeRepository.save(ReservationTime.create("10:00"));
        ReservationTimeService service = service(timeRepository, new FakeReservationRepository());

        service.delete(1L);

        assertThat(timeRepository.findAll()).isEmpty();
    }

    @Test
    void getOrCreateDefaultTime_success_when_empty_repository_and_id_is_one() {
        FakeReservationTimeRepository timeRepository = new FakeReservationTimeRepository();
        ReservationTimeService service = service(timeRepository, new FakeReservationRepository());

        ReservationTime time = service.getOrCreateDefaultTime(1L);

        assertThat(time.startAt()).isEqualTo("10:00");
        assertThat(timeRepository.findAll()).hasSize(1);
    }

    private ReservationTimeService service(
            FakeReservationTimeRepository timeRepository,
            FakeReservationRepository reservationRepository
    ) {
        return new ReservationTimeService(
                timeRepository,
                new ReservationTimeValidator(timeRepository, reservationRepository)
        );
    }

}
