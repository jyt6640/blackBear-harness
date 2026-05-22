package roomescape.member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberErrorCode;
import roomescape.member.domain.MemberRepository;

class MemberValidatorTest {

    @Test
    void validateCreatable_success() {
        MemberValidator validator = new MemberValidator(new FakeMemberRepository());

        validator.validateCreatable(command("brown@example.com"));
    }

    @Test
    void validateCreatable_fail_with_duplicate_email() {
        FakeMemberRepository memberRepository = new FakeMemberRepository();
        memberRepository.save(Member.create("브라운", "brown@example.com", "encoded-password"));
        MemberValidator validator = new MemberValidator(memberRepository);

        assertThatThrownBy(() -> validator.validateCreatable(command("brown@example.com")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.DUPLICATE_EMAIL);
    }

    private MemberCreateCommand command(String email) {
        return new MemberCreateCommand("브라운", email, "raw-password");
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
