package roomescape.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.application.AuthService;
import roomescape.auth.domain.TokenPayload;
import roomescape.member.application.dto.MemberCreateCommand;
import roomescape.member.application.MemberService;
import roomescape.member.domain.Member;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private AuthService authService;

    @Test
    void create_success() throws Exception {
        given(memberService.create(any(MemberCreateCommand.class)))
                .willReturn(Member.restore(1L, "브라운", "brown@example.com", "encoded-password"));

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "브라운",
                                  "email": "brown@example.com",
                                  "password": "password1234"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("brown@example.com"));

        verify(memberService).create(any(MemberCreateCommand.class));
    }

    @Test
    void findMe_success() throws Exception {
        given(authService.authenticate("Bearer access-token"))
                .willReturn(new TokenPayload(1L, "brown@example.com", "브라운"));
        given(memberService.getById(1L))
                .willReturn(Member.restore(1L, "브라운", "brown@example.com", "encoded-password"));

        mockMvc.perform(get("/members/me")
                        .header("Authorization", "Bearer access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("브라운"));

        verify(authService).authenticate("Bearer access-token");
        verify(memberService).getById(eq(1L));
    }
}
