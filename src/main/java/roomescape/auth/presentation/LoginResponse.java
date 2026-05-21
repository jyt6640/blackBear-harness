package roomescape.auth.presentation;

import roomescape.auth.application.LoginResult;

public record LoginResponse(String accessToken, String tokenType) {

    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken(), result.tokenType());
    }
}
