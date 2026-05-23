package roomescape.theme.domain;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class ThemeTest {

    @Test
    void create_success() {
        Theme theme = Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");

        assertThat(theme.name()).isEqualTo("잠실 미스터리");
        assertThat(theme.description()).isEqualTo("설명");
        assertThat(theme.thumbnailUrl()).isEqualTo("https://example.com/theme.jpg");
    }

}
