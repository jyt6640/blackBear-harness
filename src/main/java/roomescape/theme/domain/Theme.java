package roomescape.theme.domain;

import java.util.Objects;
import roomescape.global.exception.BadRequestException;

public class Theme {

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnailUrl;

    public Theme(Long id, String name, String description, String thumbnailUrl) {
        validateText(name);
        validateText(description);
        validateText(thumbnailUrl);
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static Theme create(String name, String description, String thumbnailUrl) {
        return new Theme(null, name, description, thumbnailUrl);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String thumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Theme theme)) {
            return false;
        }
        return id != null && Objects.equals(id, theme.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return System.identityHashCode(this);
        }
        return Objects.hash(id);
    }

    private static void validateText(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(ThemeErrorCode.INVALID_THEME);
        }
    }
}
