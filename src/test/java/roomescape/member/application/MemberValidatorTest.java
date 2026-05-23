package roomescape.member.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BusinessException;
import roomescape.member.application.dto.MemberCreateCommand;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberErrorCode;
import roomescape.member.domain.fake.FakeMemberRepository;

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

}
