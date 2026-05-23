package roomescape.member.domain.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

public class FakeMemberRepository implements MemberRepository {

    private final List<Member> members = new ArrayList<>();

    @Override
    public Member save(Member member) {
        Member saved = Member.restore((long) members.size() + 1, member.name(), member.email(), member.passwordHash());
        members.add(saved);
        return saved;
    }

    @Override
    public Optional<Member> findById(long id) {
        return members.stream()
                .filter(member -> member.id().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return members.stream()
                .filter(member -> member.email().equals(email))
                .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
