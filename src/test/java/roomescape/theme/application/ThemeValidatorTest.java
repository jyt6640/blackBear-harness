package roomescape.theme.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.fake.FakeThemeRepository;

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
        reservationRepository.markThemeInUse();
        ThemeValidator validator = new ThemeValidator(themeRepository, reservationRepository);

        assertThatThrownBy(() -> validator.validateRemovable(1L))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.THEME_IN_USE);
    }

    private Theme theme() {
        return Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

}
