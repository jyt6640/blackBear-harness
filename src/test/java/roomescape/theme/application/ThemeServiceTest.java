package roomescape.theme.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import roomescape.reservation.domain.fake.FakeReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.fake.FakeThemeRepository;

class ThemeServiceTest {

    @Test
    void create_success() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        ThemeService service = service(themeRepository, new FakeReservationRepository());

        Theme theme = service.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");

        assertThat(theme.id()).isEqualTo(1L);
        assertThat(themeRepository.findAll()).hasSize(1);
    }

    @Test
    void delete_success() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        themeRepository.save(theme());
        ThemeService service = service(themeRepository, new FakeReservationRepository());

        service.delete(1L);

        assertThat(themeRepository.findAll()).isEmpty();
    }

    @Test
    void findPopularThemes_success_with_recent_week_range() {
        FakeThemeRepository themeRepository = new FakeThemeRepository();
        ThemeService service = service(themeRepository, new FakeReservationRepository());

        service.findPopularThemes();

        assertThat(themeRepository.startInclusive()).isEqualTo(LocalDate.parse("2026-05-15"));
        assertThat(themeRepository.endInclusive()).isEqualTo(LocalDate.parse("2026-05-21"));
        assertThat(themeRepository.limit()).isEqualTo(10);
    }

    private ThemeService service(FakeThemeRepository themeRepository, FakeReservationRepository reservationRepository) {
        return new ThemeService(
                themeRepository,
                new ThemeValidator(themeRepository, reservationRepository),
                Clock.fixed(Instant.parse("2026-05-22T00:00:00Z"), ZoneId.systemDefault())
        );
    }

    private Theme theme() {
        return Theme.create("잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }

}
