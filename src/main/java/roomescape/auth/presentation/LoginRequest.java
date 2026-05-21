package roomescape.auth.presentation;

import roomescape.auth.application.LoginCommand;

public record LoginRequest(String email, String password) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
