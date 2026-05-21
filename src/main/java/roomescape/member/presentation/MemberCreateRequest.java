package roomescape.member.presentation;

import roomescape.member.application.MemberCreateCommand;

public record MemberCreateRequest(String name, String email, String password) {

    public MemberCreateCommand toCommand() {
        return new MemberCreateCommand(name, email, password);
    }
}
