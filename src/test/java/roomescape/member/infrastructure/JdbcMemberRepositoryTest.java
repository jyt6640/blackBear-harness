package roomescape.member.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import roomescape.member.domain.Member;

@JdbcTest
@Import(JdbcMemberRepository.class)
class JdbcMemberRepositoryTest {

    @Autowired
    private JdbcMemberRepository repository;

    @Test
    void save_success() {
        Member saved = repository.save(Member.create("브라운", "brown@example.com", "encoded-password"));

        assertThat(saved.id()).isNotNull();
        assertThat(repository.existsByEmail("brown@example.com")).isTrue();
    }

    @Test
    void findByEmail_success() {
        repository.save(Member.create("브라운", "brown@example.com", "encoded-password"));

        Member member = repository.findByEmail("brown@example.com").orElseThrow();

        assertThat(member.name()).isEqualTo("브라운");
    }
}
