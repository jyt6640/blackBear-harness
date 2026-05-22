package roomescape.time.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import roomescape.time.domain.ReservationTime;

@JdbcTest
@Import(JdbcReservationTimeRepository.class)
class JdbcReservationTimeRepositoryTest {

    @Autowired
    private JdbcReservationTimeRepository repository;

    @Test
    void save_success() {
        ReservationTime saved = repository.save(ReservationTime.create("10:00"));

        assertThat(saved.id()).isNotNull();
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void deleteById_success() {
        ReservationTime saved = repository.save(ReservationTime.create("10:00"));

        repository.deleteById(saved.id());

        assertThat(repository.findById(saved.id())).isEmpty();
    }
}
