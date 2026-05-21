package roomescape.member.domain;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(long id);

    Optional<Member> findByEmail(String email);

    Member save(Member member);

    boolean existsByEmail(String email);
}
