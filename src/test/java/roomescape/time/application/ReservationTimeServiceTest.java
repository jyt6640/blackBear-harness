package roomescape.time.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

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

    private static class FakeReservationTimeRepository implements ReservationTimeRepository {

        private final List<ReservationTime> times = new ArrayList<>();

        @Override
        public List<ReservationTime> findAll() {
            return times;
        }

        @Override
        public Optional<ReservationTime> findById(long id) {
            return times.stream()
                    .filter(time -> time.id().equals(id))
                    .findFirst();
        }

        @Override
        public ReservationTime save(ReservationTime reservationTime) {
            ReservationTime saved = ReservationTime.restore((long) times.size() + 1, reservationTime.startAt());
            times.add(saved);
            return saved;
        }

        @Override
        public void deleteById(long id) {
            times.removeIf(time -> time.id().equals(id));
        }
    }

    private static class FakeReservationRepository implements ReservationRepository {

        @Override
        public List<Reservation> findAll() {
            return List.of();
        }

        @Override
        public List<Reservation> findByName(String name) {
            return List.of();
        }

        @Override
        public Optional<Reservation> findById(long id) {
            return Optional.empty();
        }

        @Override
        public Reservation save(Reservation reservation) {
            return reservation;
        }

        @Override
        public void update(Reservation reservation) {
        }

        @Override
        public void deleteById(long id) {
        }

        @Override
        public boolean existsByTimeId(long timeId) {
            return false;
        }

        @Override
        public boolean existsByThemeId(long themeId) {
            return false;
        }

        @Override
        public boolean existsByDateAndTimeIdAndThemeId(String date, long timeId, long themeId) {
            return false;
        }

        @Override
        public boolean existsByDateAndTimeIdAndThemeIdExcept(String date, long timeId, long themeId, long reservationId) {
            return false;
        }

        @Override
        public List<Long> findReservedTimeIds(String date, long themeId) {
            return List.of();
        }
    }
}
