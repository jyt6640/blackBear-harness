package roomescape.member.application.dto;

public record MemberCreateCommand(String name, String email, String password) {
}
