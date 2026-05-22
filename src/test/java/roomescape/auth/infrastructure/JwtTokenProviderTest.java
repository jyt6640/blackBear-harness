package roomescape.auth.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import roomescape.auth.domain.AuthErrorCode;
import roomescape.auth.domain.TokenPayload;
import roomescape.global.exception.BusinessException;
import roomescape.member.domain.Member;

class JwtTokenProviderTest {

    @Test
    void createToken_parse_success() {
        JwtTokenProvider tokenProvider = tokenProvider(3600);

        String token = tokenProvider.createToken(new Member(1L, "브라운", "brown@example.com", "encoded-password"));
        TokenPayload payload = tokenProvider.parse(token);

        assertThat(payload.memberId()).isEqualTo(1L);
        assertThat(payload.email()).isEqualTo("brown@example.com");
    }

    @Test
    void parse_fail_with_tampered_signature() {
        JwtTokenProvider tokenProvider = tokenProvider(3600);
        String token = tokenProvider.createToken(new Member(1L, "브라운", "brown@example.com", "encoded-password"));

        assertThatThrownBy(() -> tokenProvider.parse(token + "tampered"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }

    @Test
    void parse_fail_with_expired_token() {
        JwtTokenProvider tokenProvider = tokenProvider(-1);
        String token = tokenProvider.createToken(new Member(1L, "브라운", "brown@example.com", "encoded-password"));

        assertThatThrownBy(() -> tokenProvider.parse(token))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.INVALID_TOKEN);
    }

    private JwtTokenProvider tokenProvider(long expirationSeconds) {
        return new JwtTokenProvider(
                new ObjectMapper(),
                Clock.fixed(Instant.parse("2026-05-22T00:00:00Z"), ZoneId.systemDefault()),
                "test-secret",
                expirationSeconds
        );
    }
}
