package roomescape.theme.presentation.dto;

import roomescape.theme.domain.ThemeRanking;

public record PopularThemeResponse(
        Long id,
        String name,
        String description,
        String thumbnailUrl,
        int reservationCount
) {

    public static PopularThemeResponse from(ThemeRanking ranking) {
        return new PopularThemeResponse(
                ranking.theme().id(),
                ranking.theme().name(),
                ranking.theme().description(),
                ranking.theme().thumbnailUrl(),
                ranking.reservationCount()
        );
    }
}
