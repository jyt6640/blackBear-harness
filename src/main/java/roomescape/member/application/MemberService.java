package roomescape.member.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.member.application.dto.MemberCreateCommand;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberErrorCode;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.PasswordEncoder;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, MemberValidator memberValidator) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberValidator = memberValidator;
    }

    public Member create(MemberCreateCommand command) {
        memberValidator.validateCreatable(command);
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
}
