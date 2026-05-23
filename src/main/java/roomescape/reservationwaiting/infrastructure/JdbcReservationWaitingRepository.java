package roomescape.reservationwaiting.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Repository
public class JdbcReservationWaitingRepository implements ReservationWaitingRepository {

    private static final String FIND_BY_SCHEDULE = """
            SELECT
                w.id AS waiting_id,
                w.name AS waiting_name,
                w.date AS waiting_date,
                w.sequence AS waiting_sequence,
                rt.id AS time_id,
                rt.start_at AS start_at,
                t.id AS theme_id,
                t.name AS theme_name,
                t.description AS theme_description,
                t.thumbnail_url AS theme_thumbnail_url
            FROM reservation_waiting w
            INNER JOIN reservation_time rt ON w.time_id = rt.id
            INNER JOIN theme t ON w.theme_id = t.id
            WHERE w.date = ? AND w.time_id = ? AND w.theme_id = ?
            ORDER BY w.sequence ASC
            """;

    private static final String INSERT = """
            INSERT INTO reservation_waiting (name, date, time_id, theme_id, sequence)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String DELETE_BY_ID = """
            DELETE FROM reservation_waiting
            WHERE id = ?
            """;

    private static final String ADVANCE_SEQUENCES = """
            UPDATE reservation_waiting
            SET sequence = sequence - 1
            WHERE date = ? AND time_id = ? AND theme_id = ? AND sequence > ?
            """;

    private static final String EXISTS_BY_SCHEDULE_AND_NAME = """
            SELECT COUNT(1)
            FROM reservation_waiting
            WHERE date = ? AND time_id = ? AND theme_id = ? AND name = ?
            """;

    private static final String COUNT_BY_SCHEDULE = """
            SELECT COUNT(1)
            FROM reservation_waiting
            WHERE date = ? AND time_id = ? AND theme_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ReservationWaiting> rowMapper = (resultSet, rowNumber) -> ReservationWaiting.restore(
            resultSet.getLong("waiting_id"),
            resultSet.getString("waiting_name"),
            resultSet.getString("waiting_date"),
            ReservationTime.restore(resultSet.getLong("time_id"), resultSet.getString("start_at")),
            Theme.restore(
                    resultSet.getLong("theme_id"),
                    resultSet.getString("theme_name"),
                    resultSet.getString("theme_description"),
                    resultSet.getString("theme_thumbnail_url")
            ),
            resultSet.getInt("waiting_sequence")
    );

    public JdbcReservationWaitingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReservationWaiting> findBySchedule(String date, long timeId, long themeId) {
        return jdbcTemplate.query(FIND_BY_SCHEDULE, rowMapper, date, timeId, themeId);
    }

    @Override
    public Optional<ReservationWaiting> findFirstBySchedule(String date, long timeId, long themeId) {
        return findBySchedule(date, timeId, themeId).stream().findFirst();
    }

    @Override
    public ReservationWaiting save(ReservationWaiting waiting) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, waiting.name());
            statement.setString(2, waiting.date());
            statement.setLong(3, waiting.time().id());
            statement.setLong(4, waiting.theme().id());
            statement.setInt(5, waiting.sequence());
            return statement;
        }, keyHolder);
        return ReservationWaiting.restore(
                keyHolder.getKey().longValue(),
                waiting.name(),
                waiting.date(),
                waiting.time(),
                waiting.theme(),
                waiting.sequence()
        );
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public void advanceSequences(String date, long timeId, long themeId, int deletedSequence) {
        jdbcTemplate.update(ADVANCE_SEQUENCES, date, timeId, themeId, deletedSequence);
    }

    @Override
    public boolean existsByScheduleAndName(String date, long timeId, long themeId, String name) {
        Integer count = jdbcTemplate.queryForObject(
                EXISTS_BY_SCHEDULE_AND_NAME,
                Integer.class,
                date,
                timeId,
                themeId,
                name
        );
        return count != null && count > 0;
    }

    @Override
    public int countBySchedule(String date, long timeId, long themeId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_BY_SCHEDULE, Integer.class, date, timeId, themeId);
        return count == null ? 0 : count;
    }
}
