package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.domain.ThemeRanking;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

class ReservationServiceTest {

    @Test
    void delete_success_promotes_first_waiting() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.reservations.add(reservation(1L, "브라운"));
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        waitingRepository.waitings.add(waiting(1L, "포비", 1));
        waitingRepository.waitings.add(waiting(2L, "라이언", 2));
        ReservationService service = service(reservationRepository, waitingRepository);

        service.delete(1L, null);

        assertThat(reservationRepository.deletedIds).containsExactly(1L);
        assertThat(reservationRepository.reservations).extracting(Reservation::name)
                .containsExactly("포비");
        assertThat(waitingRepository.deletedIds).containsExactly(1L);
        assertThat(waitingRepository.advanced).isTrue();
    }

    private ReservationService service(
            FakeReservationRepository reservationRepository,
            FakeReservationWaitingRepository waitingRepository
    ) {
        return new ReservationService(
                reservationRepository,
                new FakeReservationTimeRepository(),
                new FakeThemeRepository(),
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

    private class FakeReservationRepository implements ReservationRepository {

        private final List<Reservation> reservations = new ArrayList<>();
        private final List<Long> deletedIds = new ArrayList<>();

        @Override
        public List<Reservation> findAll() {
            return reservations;
        }

        @Override
        public List<Reservation> findByName(String name) {
            return List.of();
        }

        @Override
        public Optional<Reservation> findById(long id) {
            return reservations.stream()
                    .filter(reservation -> reservation.id().equals(id))
                    .findFirst();
        }

        @Override
        public Reservation save(Reservation reservation) {
            Reservation saved = Reservation.restore(
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
            deletedIds.add(id);
            reservations.removeIf(reservation -> reservation.id().equals(id));
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

    private class FakeReservationWaitingRepository implements ReservationWaitingRepository {

        private final List<ReservationWaiting> waitings = new ArrayList<>();
        private final List<Long> deletedIds = new ArrayList<>();
        private boolean advanced;

        @Override
        public List<ReservationWaiting> findBySchedule(String date, long timeId, long themeId) {
            return waitings;
        }

        @Override
        public Optional<ReservationWaiting> findFirstBySchedule(String date, long timeId, long themeId) {
            return waitings.stream().findFirst();
        }

        @Override
        public ReservationWaiting save(ReservationWaiting waiting) {
            waitings.add(waiting);
            return waiting;
        }

        @Override
        public void deleteById(long id) {
            deletedIds.add(id);
            waitings.removeIf(waiting -> waiting.id().equals(id));
        }

        @Override
        public void advanceSequences(String date, long timeId, long themeId, int deletedSequence) {
            advanced = true;
        }

        @Override
        public boolean existsByScheduleAndName(String date, long timeId, long themeId, String name) {
            return false;
        }

        @Override
        public int countBySchedule(String date, long timeId, long themeId) {
            return waitings.size();
        }
    }

    private class FakeReservationTimeRepository implements ReservationTimeRepository {

        @Override
        public List<ReservationTime> findAll() {
            return List.of(time());
        }

        @Override
        public Optional<ReservationTime> findById(long id) {
            return Optional.of(time());
        }

        @Override
        public ReservationTime save(ReservationTime reservationTime) {
            return reservationTime;
        }

        @Override
        public void deleteById(long id) {
        }
    }

    private class FakeThemeRepository implements ThemeRepository {

        @Override
        public List<Theme> findAll() {
            return List.of(theme());
        }

        @Override
        public Optional<Theme> findById(long id) {
            return Optional.of(theme());
        }

        @Override
        public Theme save(Theme theme) {
            return theme;
        }

        @Override
        public void deleteById(long id) {
        }

        @Override
        public List<ThemeRanking> findPopularThemes(java.time.LocalDate startInclusive, java.time.LocalDate endInclusive, int limit) {
            return List.of();
        }
    }
}
