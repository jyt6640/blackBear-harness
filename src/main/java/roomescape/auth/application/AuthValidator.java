package roomescape.auth.application;

import org.springframework.stereotype.Component;
import roomescape.auth.domain.AuthErrorCode;
import roomescape.global.exception.BusinessException;

@Component
public class AuthValidator {

    private static final String TOKEN_TYPE = "Bearer";
    private static final String TOKEN_PREFIX = TOKEN_TYPE + " ";

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new BusinessException(AuthErrorCode.AUTH_REQUIRED);
        }
        if (!authorizationHeader.startsWith(TOKEN_PREFIX)) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        return authorizationHeader.substring(TOKEN_PREFIX.length());
    }
}
