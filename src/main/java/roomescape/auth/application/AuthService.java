package roomescape.auth.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.application.dto.LoginCommand;
import roomescape.auth.application.dto.LoginResult;
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
    private final AuthValidator authValidator;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider,
            AuthValidator authValidator
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.authValidator = authValidator;
    }

    public LoginResult login(LoginCommand command) {
        Member member = memberRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));
        validatePassword(command.password(), member);
        return new LoginResult(tokenProvider.createToken(member), TOKEN_TYPE);
    }

    public TokenPayload authenticate(String authorizationHeader) {
        return tokenProvider.parse(authValidator.extractToken(authorizationHeader));
    }

    private void validatePassword(String rawPassword, Member member) {
        if (!member.hasSamePassword(rawPassword, passwordEncoder)) {
            throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
        }
    }
}
