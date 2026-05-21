package roomescape.theme.domain;

import roomescape.global.exception.BadRequestException;

public record Theme(Long id, String name, String description, String thumbnailUrl) {

    public Theme {
        validateText(name);
        validateText(description);
        validateText(thumbnailUrl);
    }

    public static Theme create(String name, String description, String thumbnailUrl) {
        return new Theme(null, name, description, thumbnailUrl);
    }

    private static void validateText(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(ThemeErrorCode.INVALID_THEME);
        }
    }
}
