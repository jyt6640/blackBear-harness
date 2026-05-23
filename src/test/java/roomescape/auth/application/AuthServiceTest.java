package roomescape.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.auth.application.dto.LoginCommand;
import roomescape.auth.application.dto.LoginResult;
import roomescape.auth.domain.AuthErrorCode;
import roomescape.auth.domain.TokenPayload;
import roomescape.auth.domain.TokenProvider;
import roomescape.global.exception.BusinessException;
import roomescape.member.domain.Member;
import roomescape.member.domain.PasswordEncoder;
import roomescape.member.domain.fake.FakeMemberRepository;

class AuthServiceTest {

    @Test
    void login_success() {
        FakeMemberRepository memberRepository = new FakeMemberRepository();
        memberRepository.save(Member.create("브라운", "brown@example.com", "encoded-password"));
        AuthService service = service(memberRepository);

        LoginResult result = service.login(new LoginCommand("brown@example.com", "raw-password"));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void login_fail_with_unknown_email() {
        AuthService service = service(new FakeMemberRepository());

        assertThatThrownBy(() -> service.login(new LoginCommand("brown@example.com", "raw-password")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.LOGIN_FAILED);
    }

    @Test
    void login_fail_with_wrong_password() {
        FakeMemberRepository memberRepository = new FakeMemberRepository();
        memberRepository.save(Member.create("브라운", "brown@example.com", "encoded-password"));
        AuthService service = service(memberRepository);

        assertThatThrownBy(() -> service.login(new LoginCommand("brown@example.com", "wrong-password")))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(AuthErrorCode.LOGIN_FAILED);
    }

    @Test
    void authenticate_success() {
        AuthService service = service(new FakeMemberRepository());

        TokenPayload payload = service.authenticate("Bearer access-token");

        assertThat(payload.memberId()).isEqualTo(1L);
        assertThat(payload.email()).isEqualTo("brown@example.com");
    }

    private AuthService service(FakeMemberRepository memberRepository) {
        return new AuthService(
                memberRepository,
                new FixedPasswordEncoder(),
                new FixedTokenProvider(),
                new AuthValidator()
        );
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

    private static class FixedTokenProvider implements TokenProvider {

        @Override
        public String createToken(Member member) {
            return "access-token";
        }

        @Override
        public TokenPayload parse(String token) {
            if (!token.equals("access-token")) {
                throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
            }
            return new TokenPayload(1L, "brown@example.com", "브라운");
        }
    }

}
