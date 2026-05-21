package roomescape.theme.presentation;

import roomescape.theme.domain.Theme;

public record ThemeResponse(Long id, String name, String description, String thumbnailUrl) {

    public static ThemeResponse from(Theme theme) {
        if (theme == null) {
            return null;
        }
        return new ThemeResponse(theme.id(), theme.name(), theme.description(), theme.thumbnailUrl());
    }
}
