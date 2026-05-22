package roomescape.theme.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRanking;

@JdbcTest
@Import(JdbcThemeRepository.class)
class JdbcThemeRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcThemeRepository repository;

    @Test
    void save_success() {
        Theme saved = repository.save(Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg"));

        assertThat(saved.id()).isNotNull();
        assertThat(repository.findById(saved.id())).isPresent();
    }

    @Test
    void findPopularThemes_success() {
        Theme first = repository.save(Theme.create("잠실 미스터리", "설명", "https://example.com/theme1.jpg"));
        Theme second = repository.save(Theme.create("북촌의 밤", "설명", "https://example.com/theme2.jpg"));
        insertReservation("브라운", "2026-05-20", first.id());
        insertReservation("포비", "2026-05-19", first.id());
        insertReservation("라이언", "2026-05-18", second.id());

        List<ThemeRanking> rankings = repository.findPopularThemes(
                LocalDate.parse("2026-05-15"),
                LocalDate.parse("2026-05-21"),
                10
        );

        assertThat(rankings).hasSize(2);
        assertThat(rankings.get(0).theme()).isEqualTo(first);
        assertThat(rankings.get(0).reservationCount()).isEqualTo(2);
    }

    private void insertReservation(String name, String date, long themeId) {
        jdbcTemplate.update(
                "INSERT INTO reservation (name, date, time, theme_id) VALUES (?, ?, ?, ?)",
                name,
                date,
                "10:00",
                themeId
        );
    }
}
