package roomescape.reservationwaiting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingErrorCode;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.domain.ThemeRanking;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

class ReservationWaitingServiceTest {

    @Test
    void create_success() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.reserved = true;
        FakeReservationWaitingRepository waitingRepository = new FakeReservationWaitingRepository();
        ReservationWaitingService service = service(reservationRepository, waitingRepository);

        ReservationWaiting waiting = service.create(command("브라운"));

        assertThat(waiting.sequence()).isEqualTo(1);
        assertThat(waitingRepository.waitings).hasSize(1);
    }

    @Test
    void create_success_with_next_sequence() {
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.reserved = true;
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
        reservationRepository.reserved = true;
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
        return new ReservationWaitingService(
                waitingRepository,
                reservationRepository,
                new FakeReservationTimeRepository(),
                new FakeThemeRepository(),
                new ReservationWaitingValidator(waitingRepository, reservationRepository)
        );
    }

    private ReservationWaitingCreateCommand command(String name) {
        return new ReservationWaitingCreateCommand(name, "2026-05-22", 1L, 1L);
    }

    private ReservationWaiting waiting(String name, int sequence) {
        return new ReservationWaiting(1L, name, "2026-05-22", time(), theme(), sequence);
    }

    private ReservationTime time() {
        return new ReservationTime(1L, "10:00");
    }

    private Theme theme() {
        return new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

    private class FakeReservationWaitingRepository implements ReservationWaitingRepository {

        private final List<ReservationWaiting> waitings = new ArrayList<>();

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
            ReservationWaiting saved = new ReservationWaiting(
                    (long) waitings.size() + 1,
                    waiting.name(),
                    waiting.date(),
                    waiting.time(),
                    waiting.theme(),
                    waiting.sequence()
            );
            waitings.add(saved);
            return saved;
        }

        @Override
        public void deleteById(long id) {
            waitings.removeIf(waiting -> waiting.id() == id);
        }

        @Override
        public void advanceSequences(String date, long timeId, long themeId, int deletedSequence) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean existsByScheduleAndName(String date, long timeId, long themeId, String name) {
            return waitings.stream().anyMatch(waiting -> waiting.name().equals(name));
        }

        @Override
        public int countBySchedule(String date, long timeId, long themeId) {
            return waitings.size();
        }
    }

    private class FakeReservationRepository implements ReservationRepository {

        private boolean reserved;

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
            return reserved;
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
