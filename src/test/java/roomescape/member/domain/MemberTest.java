package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BadRequestException;

class MemberTest {

    @Test
    void create_success() {
        Member member = Member.create("브라운", "brown@example.com", "encoded-password");

        assertThat(member.name()).isEqualTo("브라운");
        assertThat(member.email()).isEqualTo("brown@example.com");
        assertThat(member.passwordHash()).isEqualTo("encoded-password");
    }

    @Test
    void create_fail_with_blank_name() {
        assertThatThrownBy(() -> Member.create(" ", "brown@example.com", "encoded-password"))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.INVALID_MEMBER);
    }

    @Test
    void create_fail_with_invalid_email() {
        assertThatThrownBy(() -> Member.create("브라운", "brown", "encoded-password"))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.INVALID_MEMBER);
    }

    @Test
    void hasSamePassword_success() {
        Member member = Member.create("브라운", "brown@example.com", "encoded-password");
        PasswordEncoder passwordEncoder = new FixedPasswordEncoder();

        assertThat(member.hasSamePassword("raw-password", passwordEncoder)).isTrue();
    }

    private static class FixedPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(String rawPassword) {
            return "encoded-password";
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return rawPassword.equals("raw-password") && encodedPassword.equals("encoded-password");
        }
    }
}
