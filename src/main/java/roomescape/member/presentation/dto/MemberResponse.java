package roomescape.member.presentation.dto;

import roomescape.member.domain.Member;

public record MemberResponse(Long id, String name, String email) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.id(), member.name(), member.email());
    }
}
