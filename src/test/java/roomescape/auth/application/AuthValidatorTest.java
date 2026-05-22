package roomescape.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.auth.domain.AuthErrorCode;
import roomescape.global.exception.BusinessException;

class AuthValidatorTest {

    private final AuthValidator validator = new AuthValidator();

    @Test
    void extractToken_success() {
        String token = validator.extractToken("Bearer access-token");

        assertThat(token).isEqualTo("access-token");
    }

    @Test
    void extractToken_fail_with_blank_header() {
        assertThatThrownBy(() -> validator.extractToken(" "))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.AUTH_REQUIRED);
    }

    @Test
    void extractToken_fail_with_invalid_token_type() {
        assertThatThrownBy(() -> validator.extractToken("Basic access-token"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }
}
