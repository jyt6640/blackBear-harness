package roomescape.theme.domain.fake;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRanking;
import roomescape.theme.domain.ThemeRepository;

public class FakeThemeRepository implements ThemeRepository {

    private final List<Theme> themes = new ArrayList<>();
    private LocalDate startInclusive;
    private LocalDate endInclusive;
    private int limit;

    public LocalDate startInclusive() {
        return startInclusive;
    }

    public LocalDate endInclusive() {
        return endInclusive;
    }

    public int limit() {
        return limit;
    }

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
