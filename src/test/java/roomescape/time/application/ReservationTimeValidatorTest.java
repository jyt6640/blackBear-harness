package roomescape.time.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

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
        reservationRepository.timeInUse = true;
        ReservationTimeValidator validator = new ReservationTimeValidator(timeRepository, reservationRepository);

        assertThatThrownBy(() -> validator.validateRemovable(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.TIME_IN_USE);
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

        private boolean timeInUse;

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
            return timeInUse;
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
