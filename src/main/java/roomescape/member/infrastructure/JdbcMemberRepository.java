package roomescape.member.infrastructure;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private static final String FIND_BY_ID = """
            SELECT id, name, email, password_hash
            FROM member_account
            WHERE id = ?
            """;

    private static final String FIND_BY_EMAIL = """
            SELECT id, name, email, password_hash
            FROM member_account
            WHERE email = ?
            """;

    private static final String INSERT = """
            INSERT INTO member_account (name, email, password_hash)
            VALUES (?, ?, ?)
            """;

    private static final String EXISTS_BY_EMAIL = """
            SELECT COUNT(1)
            FROM member_account
            WHERE email = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Member> findById(long id) {
        return query(FIND_BY_ID, id).stream().findFirst();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return query(FIND_BY_EMAIL, email).stream().findFirst();
    }

    @Override
    public Member save(Member member) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, member.name());
            statement.setString(2, member.email());
            statement.setString(3, member.passwordHash());
            return statement;
        }, keyHolder);
        return Member.restore(keyHolder.getKey().longValue(), member.name(), member.email(), member.passwordHash());
    }

    @Override
    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_BY_EMAIL, Integer.class, email);
        return count != null && count > 0;
    }

    private List<Member> query(String sql, Object... args) {
        return jdbcTemplate.query(
                sql,
                (resultSet, rowNumber) -> Member.restore(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("password_hash")
                ),
                args
        );
    }
}
