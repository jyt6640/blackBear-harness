package roomescape.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import roomescape.auth.application.dto.LoginCommand;

public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
