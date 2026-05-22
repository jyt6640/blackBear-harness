package roomescape.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.member.application.dto.MemberCreateCommand;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberErrorCode;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.PasswordEncoder;

class MemberServiceTest {

    @Test
    void create_success() {
        FakeMemberRepository memberRepository = new FakeMemberRepository();
        MemberService service = service(memberRepository);

        Member member = service.create(new MemberCreateCommand("브라운", "brown@example.com", "raw-password"));

        assertThat(member.id()).isEqualTo(1L);
        assertThat(member.passwordHash()).isEqualTo("encoded-raw-password");
    }

    @Test
    void getById_fail_with_not_found_member() {
        MemberService service = service(new FakeMemberRepository());

        assertThatThrownBy(() -> service.getById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    private MemberService service(FakeMemberRepository memberRepository) {
        return new MemberService(
                memberRepository,
                new FixedPasswordEncoder(),
                new MemberValidator(memberRepository)
        );
    }

    private static class FixedPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(String rawPassword) {
            return "encoded-" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return encode(rawPassword).equals(encodedPassword);
        }
    }

    private static class FakeMemberRepository implements MemberRepository {

        private final List<Member> members = new ArrayList<>();

        @Override
        public Member save(Member member) {
            Member saved = new Member((long) members.size() + 1, member.name(), member.email(), member.passwordHash());
            members.add(saved);
            return saved;
        }

        @Override
        public Optional<Member> findById(long id) {
            return members.stream()
                    .filter(member -> member.id().equals(id))
                    .findFirst();
        }

        @Override
        public Optional<Member> findByEmail(String email) {
            return members.stream()
                    .filter(member -> member.email().equals(email))
                    .findFirst();
        }

        @Override
        public boolean existsByEmail(String email) {
            return findByEmail(email).isPresent();
        }
    }
}
