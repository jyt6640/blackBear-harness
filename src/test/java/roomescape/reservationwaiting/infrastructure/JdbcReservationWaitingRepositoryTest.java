package roomescape.reservationwaiting.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@JdbcTest
@Import(JdbcReservationWaitingRepository.class)
class JdbcReservationWaitingRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcReservationWaitingRepository repository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(
                "INSERT INTO reservation_time (id, start_at) VALUES (?, ?)",
                1L,
                "10:00"
        );
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
        ReservationWaiting saved = repository.save(waiting("브라운", 1));

        assertThat(saved.id()).isNotNull();
        assertThat(repository.countBySchedule("2026-05-22", 1L, 1L)).isEqualTo(1);
    }

    @Test
    void findBySchedule_success() {
        repository.save(waiting("브라운", 1));
        repository.save(waiting("포비", 2));

        List<ReservationWaiting> waitings = repository.findBySchedule("2026-05-22", 1L, 1L);

        assertThat(waitings).extracting(ReservationWaiting::name)
                .containsExactly("브라운", "포비");
    }

    @Test
    void findFirstBySchedule_success() {
        repository.save(waiting("브라운", 1));
        repository.save(waiting("포비", 2));

        ReservationWaiting waiting = repository.findFirstBySchedule("2026-05-22", 1L, 1L).orElseThrow();

        assertThat(waiting.name()).isEqualTo("브라운");
    }

    @Test
    void advanceSequences_success() {
        ReservationWaiting first = repository.save(waiting("브라운", 1));
        repository.save(waiting("포비", 2));
        repository.deleteById(first.id());

        repository.advanceSequences("2026-05-22", 1L, 1L, first.sequence());

        List<ReservationWaiting> waitings = repository.findBySchedule("2026-05-22", 1L, 1L);
        assertThat(waitings).hasSize(1);
        assertThat(waitings.get(0).sequence()).isEqualTo(1);
    }

    private ReservationWaiting waiting(String name, int sequence) {
        return ReservationWaiting.create(
                name,
                "2026-05-22",
                new ReservationTime(1L, "10:00"),
                new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg"),
                sequence
        );
    }
}
