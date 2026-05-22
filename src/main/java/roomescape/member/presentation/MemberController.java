package roomescape.member.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.application.AuthService;
import roomescape.auth.domain.TokenPayload;
import roomescape.member.application.MemberService;
import roomescape.member.presentation.dto.MemberCreateRequest;
import roomescape.member.presentation.dto.MemberResponse;

@RestController
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    public MemberController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @PostMapping("/members")
    public MemberResponse create(@RequestBody MemberCreateRequest request) {
        return MemberResponse.from(memberService.create(request.toCommand()));
    }

    @GetMapping("/members/me")
    public MemberResponse findMe(@RequestHeader(value = "Authorization", required = false) String authorization) {
        TokenPayload payload = authService.authenticate(authorization);
        return MemberResponse.from(memberService.getById(payload.memberId()));
    }
}
