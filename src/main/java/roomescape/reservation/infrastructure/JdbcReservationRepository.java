package roomescape.reservation.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Repository
public class JdbcReservationRepository implements ReservationRepository {

    private static final String FIND_ALL = """
            SELECT
                r.id AS reservation_id,
                r.name AS reservation_name,
                r.date AS reservation_date,
                r.time AS legacy_time,
                rt.id AS time_id,
                rt.start_at AS start_at,
                t.id AS theme_id,
                t.name AS theme_name,
                t.description AS theme_description,
                t.thumbnail_url AS theme_thumbnail_url
            FROM reservation r
            LEFT JOIN reservation_time rt ON r.time_id = rt.id
            LEFT JOIN theme t ON r.theme_id = t.id
            ORDER BY r.id
            """;

    private static final String FIND_BY_ID = FIND_ALL.replace("ORDER BY r.id", "WHERE r.id = ?");

    private static final String FIND_BY_NAME = """
            SELECT
                r.id AS reservation_id,
                r.name AS reservation_name,
                r.date AS reservation_date,
                r.time AS legacy_time,
                rt.id AS time_id,
                rt.start_at AS start_at,
                t.id AS theme_id,
                t.name AS theme_name,
                t.description AS theme_description,
                t.thumbnail_url AS theme_thumbnail_url
            FROM reservation r
            LEFT JOIN reservation_time rt ON r.time_id = rt.id
            LEFT JOIN theme t ON r.theme_id = t.id
            WHERE r.name = ?
            ORDER BY r.id
            """;

    private static final String INSERT = """
            INSERT INTO reservation (name, date, time, time_id, theme_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE reservation
            SET date = ?, time = ?, time_id = ?, theme_id = ?
            WHERE id = ?
            """;

    private static final String DELETE_BY_ID = """
            DELETE FROM reservation
            WHERE id = ?
            """;

    private static final String EXISTS_BY_TIME_ID = """
            SELECT COUNT(1)
            FROM reservation
            WHERE time_id = ?
            """;

    private static final String EXISTS_BY_THEME_ID = """
            SELECT COUNT(1)
            FROM reservation
            WHERE theme_id = ?
            """;

    private static final String EXISTS_DUPLICATE = """
            SELECT COUNT(1)
            FROM reservation
            WHERE date = ? AND time_id = ? AND theme_id = ?
            """;

    private static final String EXISTS_DUPLICATE_EXCEPT = """
            SELECT COUNT(1)
            FROM reservation
            WHERE date = ? AND time_id = ? AND theme_id = ? AND id <> ?
            """;

    private static final String FIND_RESERVED_TIME_IDS = """
            SELECT time_id
            FROM reservation
            WHERE date = ? AND theme_id = ? AND time_id IS NOT NULL
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Reservation> rowMapper = (resultSet, rowNumber) -> {
        Long timeId = resultSet.getObject("time_id", Long.class);
        Long themeId = resultSet.getObject("theme_id", Long.class);
        ReservationTime time = null;
        Theme theme = null;
        if (timeId != null) {
            time = ReservationTime.restore(timeId, resultSet.getString("start_at"));
        }
        if (themeId != null) {
            theme = Theme.restore(
                    themeId,
                    resultSet.getString("theme_name"),
                    resultSet.getString("theme_description"),
                    resultSet.getString("theme_thumbnail_url")
            );
        }
        return Reservation.restore(
                resultSet.getLong("reservation_id"),
                resultSet.getString("reservation_name"),
                resultSet.getString("reservation_date"),
                resultSet.getString("legacy_time"),
                time,
                theme
        );
    };

    public JdbcReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Reservation> findAll() {
        return jdbcTemplate.query(FIND_ALL, rowMapper);
    }

    @Override
    public List<Reservation> findByName(String name) {
        return jdbcTemplate.query(FIND_BY_NAME, rowMapper, name);
    }

    @Override
    public Optional<Reservation> findById(long id) {
        return jdbcTemplate.query(FIND_BY_ID, rowMapper, id).stream().findFirst();
    }

    @Override
    public Reservation save(Reservation reservation) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, reservation.name());
            statement.setString(2, reservation.date());
            statement.setString(3, reservation.legacyTime());
            statement.setObject(4, timeId(reservation));
            statement.setObject(5, themeId(reservation));
            return statement;
        }, keyHolder);
        return findById(keyHolder.getKey().longValue()).orElseThrow();
    }

    @Override
    public void update(Reservation reservation) {
        jdbcTemplate.update(
                UPDATE,
                reservation.date(),
                reservation.legacyTime(),
                timeId(reservation),
                themeId(reservation),
                reservation.id()
        );
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public boolean existsByTimeId(long timeId) {
        return count(EXISTS_BY_TIME_ID, timeId) > 0;
    }

    @Override
    public boolean existsByThemeId(long themeId) {
        return count(EXISTS_BY_THEME_ID, themeId) > 0;
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeId(String date, long timeId, long themeId) {
        return count(EXISTS_DUPLICATE, date, timeId, themeId) > 0;
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeIdExcept(String date, long timeId, long themeId, long reservationId) {
        return count(EXISTS_DUPLICATE_EXCEPT, date, timeId, themeId, reservationId) > 0;
    }

    @Override
    public List<Long> findReservedTimeIds(String date, long themeId) {
        return jdbcTemplate.query(FIND_RESERVED_TIME_IDS, (resultSet, rowNumber) -> resultSet.getLong("time_id"), date, themeId);
    }

    private Integer count(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, Integer.class, args);
    }

    private Long timeId(Reservation reservation) {
        if (reservation.time() == null) {
            return null;
        }
        return reservation.time().id();
    }

    private Long themeId(Reservation reservation) {
        if (reservation.theme() == null) {
            return null;
        }
        return reservation.theme().id();
    }
}
