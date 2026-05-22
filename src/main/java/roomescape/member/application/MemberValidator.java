package roomescape.member.application;

import org.springframework.stereotype.Component;
import roomescape.global.exception.BusinessException;
import roomescape.member.application.dto.MemberCreateCommand;
import roomescape.member.domain.MemberErrorCode;
import roomescape.member.domain.MemberRepository;

@Component
public class MemberValidator {

    private final MemberRepository memberRepository;

    public MemberValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void validateCreatable(MemberCreateCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}
