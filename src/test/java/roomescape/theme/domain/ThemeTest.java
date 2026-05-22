package roomescape.theme.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BadRequestException;

class ThemeTest {

    @Test
    void create_success() {
        Theme theme = Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");

        assertThat(theme.name()).isEqualTo("잠실 미스터리");
        assertThat(theme.description()).isEqualTo("설명");
        assertThat(theme.thumbnailUrl()).isEqualTo("https://example.com/theme.jpg");
    }

    @Test
    void create_fail_with_blank_name() {
        assertThatThrownBy(() -> Theme.create(" ", "설명", "https://example.com/theme.jpg"))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.INVALID_THEME);
    }

    @Test
    void create_fail_with_blank_description() {
        assertThatThrownBy(() -> Theme.create("잠실 미스터리", " ", "https://example.com/theme.jpg"))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.INVALID_THEME);
    }

    @Test
    void create_fail_with_blank_thumbnailUrl() {
        assertThatThrownBy(() -> Theme.create("잠실 미스터리", "설명", " "))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ThemeErrorCode.INVALID_THEME);
    }
}
