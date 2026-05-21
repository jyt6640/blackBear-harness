package roomescape.member.application;

public record MemberCreateCommand(String name, String email, String password) {
}
