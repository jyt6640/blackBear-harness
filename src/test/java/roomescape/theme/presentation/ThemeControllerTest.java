package roomescape.theme.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.theme.application.ThemeService;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRanking;

@WebMvcTest(ThemeController.class)
class ThemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThemeService themeService;

    @Test
    void findAll_success() throws Exception {
        given(themeService.findAll())
                .willReturn(List.of(theme()));

        mockMvc.perform(get("/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("잠실 미스터리"));
    }

    @Test
    void create_success() throws Exception {
        given(themeService.create("잠실 미스터리", "설명", "https://example.com/theme.jpg"))
                .willReturn(theme());

        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "잠실 미스터리",
                                  "description": "설명",
                                  "thumbnailUrl": "https://example.com/theme.jpg"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/themes/1"))
                .andExpect(status().isOk());

        verify(themeService).delete(1L);
    }

    @Test
    void findPopularThemes_success() throws Exception {
        given(themeService.findPopularThemes())
                .willReturn(List.of(new ThemeRanking(theme(), 3)));

        mockMvc.perform(get("/themes/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservationCount").value(3));
    }

    private Theme theme() {
        return Theme.restore(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }
}
