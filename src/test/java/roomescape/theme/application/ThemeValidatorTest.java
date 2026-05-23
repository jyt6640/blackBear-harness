package roomescape.theme.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.ThemeRanking;
import roomescape.theme.domain.ThemeRepository;

class ThemeValidatorTest {

    @Test
    void validateRemovable_success() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        themeRepository.save(theme());
        ThemeValidator validator = new ThemeValidator(themeRepository, new FakeReservationRepository());

        validator.validateRemovable(1L);
    }

    @Test
    void validateRemovable_fail_with_not_found_theme() {
        ThemeValidator validator = new ThemeValidator(new FakeThemeRepository(), new FakeReservationRepository());

        assertThatThrownBy(() -> validator.validateRemovable(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.THEME_NOT_FOUND);
    }

    @Test
    void validateRemovable_fail_with_theme_in_use() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        themeRepository.save(theme());
        FakeReservationRepository reservationRepository = new FakeReservationRepository();
        reservationRepository.themeInUse = true;
        ThemeValidator validator = new ThemeValidator(themeRepository, reservationRepository);

        assertThatThrownBy(() -> validator.validateRemovable(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.THEME_IN_USE);
    }

    private Theme theme() {
        return Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

    private static class FakeThemeRepository implements ThemeRepository {

        private final List<Theme> themes = new ArrayList<>();

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
            return List.of();
        }
    }

    private static class FakeReservationRepository implements ReservationRepository {

        private boolean themeInUse;

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
            return themeInUse;
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
