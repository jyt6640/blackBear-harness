package roomescape.theme.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRanking;
import roomescape.theme.domain.ThemeRepository;

class ThemeServiceTest {

    @Test
    void create_success() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        ThemeService service = service(themeRepository, new FakeReservationRepository());

        Theme theme = service.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");

        assertThat(theme.id()).isEqualTo(1L);
        assertThat(themeRepository.findAll()).hasSize(1);
    }

    @Test
    void delete_success() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        themeRepository.save(theme());
        ThemeService service = service(themeRepository, new FakeReservationRepository());

        service.delete(1L);

        assertThat(themeRepository.findAll()).isEmpty();
    }

    @Test
    void findPopularThemes_success_with_recent_week_range() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        ThemeService service = service(themeRepository, new FakeReservationRepository());

        service.findPopularThemes();

        assertThat(themeRepository.startInclusive).isEqualTo(LocalDate.parse("2026-05-15"));
        assertThat(themeRepository.endInclusive).isEqualTo(LocalDate.parse("2026-05-21"));
        assertThat(themeRepository.limit).isEqualTo(10);
    }

    private ThemeService service(FakeThemeRepository themeRepository, FakeReservationRepository reservationRepository) {
        return new ThemeService(
                themeRepository,
                new ThemeValidator(themeRepository, reservationRepository),
                Clock.fixed(Instant.parse("2026-05-22T00:00:00Z"), ZoneId.systemDefault())
        );
    }

    private Theme theme() {
        return Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

    private static class FakeThemeRepository implements ThemeRepository {

        private final List<Theme> themes = new ArrayList<>();
        private LocalDate startInclusive;
        private LocalDate endInclusive;
        private int limit;

        @Override
        public List<Theme> findAll() {
            return themes;
        }

        @Override
        public Optional<Theme> findById(long id) {
            return themes.stream()
                    .filter(theme -> theme.id().equals(id))
                    .findFirst();
        }

        @Override
        public Theme save(Theme theme) {
            Theme saved = Theme.restore((long) themes.size() + 1, theme.name(), theme.description(), theme.thumbnailUrl());
            themes.add(saved);
            return saved;
        }

        @Override
        public void deleteById(long id) {
            themes.removeIf(theme -> theme.id().equals(id));
        }

        @Override
        public List<ThemeRanking> findPopularThemes(LocalDate startInclusive, LocalDate endInclusive, int limit) {
            this.startInclusive = startInclusive;
            this.endInclusive = endInclusive;
            this.limit = limit;
            return List.of();
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
