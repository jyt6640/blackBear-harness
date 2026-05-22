package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationErrorCode;
import roomescape.reservation.domain.ReservationRepository;
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
        return new Reservation(id, "브라운", "2026-05-22", null, time(), theme());
    }

    private ReservationTime time() {
        return new ReservationTime(1L, "10:00");
    }

    private Theme theme() {
        return new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

    private static class FakeReservationRepository implements ReservationRepository {

        private final List<Reservation> reservations = new ArrayList<>();

        @Override
        public List<Reservation> findAll() {
            return reservations;
        }

        @Override
        public List<Reservation> findByName(String name) {
            return reservations.stream()
                    .filter(reservation -> reservation.name().equals(name))
                    .toList();
        }

        @Override
        public Optional<Reservation> findById(long id) {
            return reservations.stream()
                    .filter(reservation -> reservation.id().equals(id))
                    .findFirst();
        }

        @Override
        public Reservation save(Reservation reservation) {
            Reservation saved = new Reservation(
                    (long) reservations.size() + 1,
                    reservation.name(),
                    reservation.date(),
                    reservation.legacyTime(),
                    reservation.time(),
                    reservation.theme()
            );
            reservations.add(saved);
            return saved;
        }

        @Override
        public void update(Reservation reservation) {
        }

        @Override
        public void deleteById(long id) {
            reservations.removeIf(reservation -> reservation.id().equals(id));
        }

        @Override
        public boolean existsByTimeId(long timeId) {
            return reservations.stream().anyMatch(reservation -> reservation.time().id().equals(timeId));
        }

        @Override
        public boolean existsByThemeId(long themeId) {
            return reservations.stream().anyMatch(reservation -> reservation.theme().id().equals(themeId));
        }

        @Override
        public boolean existsByDateAndTimeIdAndThemeId(String date, long timeId, long themeId) {
            return reservations.stream().anyMatch(reservation -> hasSameSchedule(reservation, date, timeId, themeId));
        }

        @Override
        public boolean existsByDateAndTimeIdAndThemeIdExcept(String date, long timeId, long themeId, long reservationId) {
            return reservations.stream()
                    .filter(reservation -> !reservation.id().equals(reservationId))
                    .anyMatch(reservation -> hasSameSchedule(reservation, date, timeId, themeId));
        }

        @Override
        public List<Long> findReservedTimeIds(String date, long themeId) {
            return reservations.stream()
                    .filter(reservation -> reservation.date().equals(date))
                    .filter(reservation -> reservation.theme().id().equals(themeId))
                    .map(reservation -> reservation.time().id())
                    .toList();
        }

        private boolean hasSameSchedule(Reservation reservation, String date, long timeId, long themeId) {
            return reservation.date().equals(date)
                    && reservation.time().id().equals(timeId)
                    && reservation.theme().id().equals(themeId);
        }
    }
}
