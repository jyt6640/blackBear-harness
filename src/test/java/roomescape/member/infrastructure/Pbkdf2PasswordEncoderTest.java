package roomescape.member.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class Pbkdf2PasswordEncoderTest {

    private final Pbkdf2PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder();

    @Test
    void encode_success() {
        String encodedPassword = passwordEncoder.encode("password1234");

        assertThat(passwordEncoder.matches("password1234", encodedPassword)).isTrue();
        assertThat(passwordEncoder.matches("wrong-password", encodedPassword)).isFalse();
    }

    @Test
    void encode_fail_with_short_password() {
        assertThatThrownBy(() -> passwordEncoder.encode("short"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
