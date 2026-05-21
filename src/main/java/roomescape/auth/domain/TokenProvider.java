package roomescape.auth.domain;

import roomescape.member.domain.Member;

public interface TokenProvider {

    String createToken(Member member);

    TokenPayload parse(String token);
}
