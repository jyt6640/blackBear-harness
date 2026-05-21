package roomescape.auth.domain;

public record TokenPayload(long memberId, String email, String name) {
}
