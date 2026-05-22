package roomescape.theme.application;

import org.springframework.stereotype.Component;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.ThemeRepository;

@Component
public class ThemeValidator {

    private final ThemeRepository themeRepository;
    private final ReservationRepository reservationRepository;

    public ThemeValidator(ThemeRepository themeRepository, ReservationRepository reservationRepository) {
        this.themeRepository = themeRepository;
        this.reservationRepository = reservationRepository;
    }

    public void validateRemovable(long id) {
        validateExists(id);
        validateNotUsed(id);
    }

    private void validateExists(long id) {
        themeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ThemeErrorCode.THEME_NOT_FOUND));
    }

    private void validateNotUsed(long id) {
        if (reservationRepository.existsByThemeId(id)) {
            throw new BusinessException(ThemeErrorCode.THEME_IN_USE);
        }
    }
}
