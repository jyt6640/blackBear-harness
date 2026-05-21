package roomescape.theme.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRanking;
import roomescape.theme.domain.ThemeRepository;

@Repository
public class JdbcThemeRepository implements ThemeRepository {

    private static final String FIND_ALL = """
            SELECT id, name, description, thumbnail_url
            FROM theme
            ORDER BY id
            """;

    private static final String FIND_BY_ID = """
            SELECT id, name, description, thumbnail_url
            FROM theme
            WHERE id = ?
            """;

    private static final String INSERT = """
            INSERT INTO theme (name, description, thumbnail_url)
            VALUES (?, ?, ?)
            """;

    private static final String DELETE_BY_ID = """
            DELETE FROM theme
            WHERE id = ?
            """;

    private static final String FIND_POPULAR_THEMES = """
            SELECT t.id, t.name, t.description, t.thumbnail_url, COUNT(r.id) AS reservation_count
            FROM theme t
            INNER JOIN reservation r ON r.theme_id = t.id
            WHERE PARSEDATETIME(r.date, 'yyyy-MM-dd') BETWEEN ? AND ?
            GROUP BY t.id, t.name, t.description, t.thumbnail_url
            ORDER BY reservation_count DESC, t.id ASC
            LIMIT ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcThemeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Theme> findAll() {
        return jdbcTemplate.query(FIND_ALL, (resultSet, rowNumber) -> new Theme(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("thumbnail_url")
        ));
    }

    @Override
    public Optional<Theme> findById(long id) {
        List<Theme> themes = jdbcTemplate.query(
                FIND_BY_ID,
                (resultSet, rowNumber) -> new Theme(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getString("thumbnail_url")
                ),
                id
        );
        return themes.stream().findFirst();
    }

    @Override
    public Theme save(Theme theme) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, theme.name());
            statement.setString(2, theme.description());
            statement.setString(3, theme.thumbnailUrl());
            return statement;
        }, keyHolder);
        return new Theme(keyHolder.getKey().longValue(), theme.name(), theme.description(), theme.thumbnailUrl());
    }

    @Override
    public void deleteById(long id) {
        jdbcTemplate.update(DELETE_BY_ID, id);
    }

    @Override
    public List<ThemeRanking> findPopularThemes(LocalDate startInclusive, LocalDate endInclusive, int limit) {
        return jdbcTemplate.query(
                FIND_POPULAR_THEMES,
                (resultSet, rowNumber) -> new ThemeRanking(
                        new Theme(
                                resultSet.getLong("id"),
                                resultSet.getString("name"),
                                resultSet.getString("description"),
                                resultSet.getString("thumbnail_url")
                        ),
                        resultSet.getInt("reservation_count")
                ),
                startInclusive,
                endInclusive,
                limit
        );
    }
}
