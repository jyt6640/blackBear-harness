package roomescape.member.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import roomescape.member.application.dto.MemberCreateCommand;

public record MemberCreateRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password
) {

    public MemberCreateCommand toCommand() {
        return new MemberCreateCommand(name, email, password);
    }
}
