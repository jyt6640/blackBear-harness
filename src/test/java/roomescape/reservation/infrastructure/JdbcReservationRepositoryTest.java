package roomescape.reservation.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@JdbcTest
@Import(JdbcReservationRepository.class)
class JdbcReservationRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcReservationRepository repository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO reservation_time (id, start_at) VALUES (?, ?)", 1L, "10:00");
        jdbcTemplate.update(
                "INSERT INTO theme (id, name, description, thumbnail_url) VALUES (?, ?, ?, ?)",
                1L,
                "잠실 미스터리",
                "설명",
                "https://example.com/theme.jpg"
        );
    }

    @Test
    void save_success() {
        Reservation saved = repository.save(reservation(null));

        assertThat(saved.id()).isNotNull();
        assertThat(saved.time()).isEqualTo(time());
        assertThat(saved.theme()).isEqualTo(theme());
    }

    @Test
    void update_success() {
        Reservation saved = repository.save(reservation(null));
        Reservation changed = new Reservation(saved.id(), "브라운", "2026-05-23", null, time(), theme());

        repository.update(changed);

        assertThat(repository.findById(saved.id()).orElseThrow().date()).isEqualTo("2026-05-23");
    }

    @Test
    void findReservedTimeIds_success() {
        repository.save(reservation(null));

        List<Long> timeIds = repository.findReservedTimeIds("2026-05-22", 1L);

        assertThat(timeIds).containsExactly(1L);
    }

    private Reservation reservation(Long id) {
        return new Reservation(id, "브라운", "2026-05-22", null, time(), theme());
    }

    private ReservationTime time() {
        return new ReservationTime(1L, "10:00");
    }

    private Theme theme() {
        return new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }
}
