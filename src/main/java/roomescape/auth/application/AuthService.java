package roomescape.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.domain.AuthErrorCode;
import roomescape.auth.domain.TokenPayload;
import roomescape.auth.domain.TokenProvider;
import roomescape.global.exception.BusinessException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.PasswordEncoder;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public LoginResult login(LoginCommand command) {
        Member member = memberRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));
        validatePassword(command.password(), member);
        return new LoginResult(tokenProvider.createToken(member), TOKEN_TYPE);
    }

    public TokenPayload authenticate(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new BusinessException(AuthErrorCode.AUTH_REQUIRED);
        }
        if (!authorizationHeader.startsWith(TOKEN_TYPE + " ")) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        return tokenProvider.parse(authorizationHeader.substring((TOKEN_TYPE + " ").length()));
    }

    private void validatePassword(String rawPassword, Member member) {
        if (!member.hasSamePassword(rawPassword, passwordEncoder)) {
            throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
        }
    }
}
