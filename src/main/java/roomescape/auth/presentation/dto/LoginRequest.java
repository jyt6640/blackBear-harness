package roomescape.auth.presentation.dto;

import roomescape.auth.application.dto.LoginCommand;

public record LoginRequest(String email, String password) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
