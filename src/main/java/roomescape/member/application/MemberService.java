package roomescape.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberErrorCode;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.PasswordEncoder;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member create(MemberCreateCommand command) {
        validateUniqueEmail(command.email());
        return memberRepository.save(Member.create(
                command.name(),
                command.email(),
                passwordEncoder.encode(command.password())
        ));
    }

    @Transactional(readOnly = true)
    public Member getById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateUniqueEmail(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}
