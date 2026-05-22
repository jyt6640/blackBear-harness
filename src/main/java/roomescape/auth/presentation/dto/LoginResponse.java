package roomescape.auth.presentation.dto;

import roomescape.auth.application.dto.LoginResult;

public record LoginResponse(String accessToken, String tokenType) {

    public static LoginResponse from(LoginResult result) {
        return new LoginResponse(result.accessToken(), result.tokenType());
    }
}
