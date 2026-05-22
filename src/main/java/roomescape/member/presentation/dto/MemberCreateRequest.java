package roomescape.member.presentation.dto;

import roomescape.member.application.dto.MemberCreateCommand;

public record MemberCreateRequest(String name, String email, String password) {

    public MemberCreateCommand toCommand() {
        return new MemberCreateCommand(name, email, password);
    }
}
