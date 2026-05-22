package roomescape.time.presentation;

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
import roomescape.time.application.ReservationTimeService;
import roomescape.time.domain.ReservationTime;

@WebMvcTest(ReservationTimeController.class)
class ReservationTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationTimeService reservationTimeService;

    @Test
    void findAll_success() throws Exception {
        given(reservationTimeService.findAll())
                .willReturn(List.of(new ReservationTime(1L, "10:00")));

        mockMvc.perform(get("/times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startAt").value("10:00"));
    }

    @Test
    void create_success() throws Exception {
        given(reservationTimeService.create("10:00"))
                .willReturn(new ReservationTime(1L, "10:00"));

        mockMvc.perform(post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startAt": "10:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/times/1"))
                .andExpect(status().isOk());

        verify(reservationTimeService).delete(1L);
    }
}
