package roomescape.theme.application;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.ThemeRanking;
import roomescape.theme.domain.ThemeRepository;

@Service
@Transactional
public class ThemeService {

    private static final int POPULAR_THEME_LIMIT = 10;

    private final ThemeRepository themeRepository;
    private final ThemeValidator themeValidator;
    private final Clock clock;

    public ThemeService(ThemeRepository themeRepository, ThemeValidator themeValidator, Clock clock) {
        this.themeRepository = themeRepository;
        this.themeValidator = themeValidator;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ThemeRanking> findPopularThemes() {
        LocalDate yesterday = LocalDate.now(clock).minusDays(1);
        LocalDate weekAgo = LocalDate.now(clock).minusDays(7);
        return themeRepository.findPopularThemes(weekAgo, yesterday, POPULAR_THEME_LIMIT);
    }

    @Transactional(readOnly = true)
    public Theme getById(long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ThemeErrorCode.THEME_NOT_FOUND));
    }

    public Theme create(String name, String description, String thumbnailUrl) {
        return themeRepository.save(Theme.create(name, description, thumbnailUrl));
    }

    public void delete(long id) {
        themeValidator.validateRemovable(id);
        themeRepository.deleteById(id);
    }
}
