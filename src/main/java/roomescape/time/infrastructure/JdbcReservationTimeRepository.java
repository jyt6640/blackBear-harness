package roomescape.time.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

@Repository
public class JdbcReservationTimeRepository implements ReservationTimeRepository {

    private static final String FIND_ALL = """
            SELECT id, start_at
            FROM reservation_time
            ORDER BY start_at
            """;

    private static final String FIND_BY_ID = """
            SELECT id, start_at
            FROM reservation_time
            WHERE id = ?
            """;

    private static final String INSERT = """
            INSERT INTO reservation_time (start_at)
            VALUES (?)
            """;

    private static final String DELETE_BY_ID = """
            DELETE FROM reservation_time
            WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcReservationTimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReservationTime> findAll() {
        return jdbcTemplate.query(FIND_ALL, (resultSet, rowNumber) -> new ReservationTime(
                resultSet.getLong("id"),
                resultSet.getString("start_at")
        ));
    }

    @Override
    public Optional<ReservationTime> findById(long id) {
        List<ReservationTime> reservationTimes = jdbcTemplate.query(
                FIND_BY_ID,
                (resultSet, rowNumber) -> new ReservationTime(
                        resultSet.getLong("id"),
                        resultSet.getString("start_at")
                ),
                id
        );
        return reservationTimes.stream().findFirst();
    }

    @Override
    public ReservationTime save(ReservationTime reservationTime) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, reservationTime.startAt());
            return statement;
        }, keyHolder);
        return new ReservationTime(keyHolder.getKey().longValue(), reservationTime.startAt());
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }
}
