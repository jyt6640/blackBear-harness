package roomescape.reservationwaiting.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.reservationwaiting.application.dto.ReservationWaitingCreateCommand;
import roomescape.reservationwaiting.application.ReservationWaitingService;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@WebMvcTest(ReservationWaitingController.class)
class ReservationWaitingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationWaitingService service;

    @Test
    void create_success() throws Exception {
        given(service.create(any(ReservationWaitingCreateCommand.class)))
                .willReturn(waiting("브라운", 1));

        mockMvc.perform(post("/reservation-waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "브라운",
                                  "date": "2026-05-22",
                                  "timeId": 1,
                                  "themeId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("브라운"))
                .andExpect(jsonPath("$.sequence").value(1));

        verify(service).create(any(ReservationWaitingCreateCommand.class));
    }

    @Test
    void findBySchedule_success() throws Exception {
        given(service.findBySchedule("2026-05-22", 1L, 1L))
                .willReturn(List.of(waiting("브라운", 1), waiting("포비", 2)));

        mockMvc.perform(get("/reservation-waitings")
                        .param("date", "2026-05-22")
                        .param("timeId", "1")
                        .param("themeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sequence").value(1))
                .andExpect(jsonPath("$[1].sequence").value(2));
    }

    private ReservationWaiting waiting(String name, int sequence) {
        return new ReservationWaiting(
                (long) sequence,
                name,
                "2026-05-22",
                new ReservationTime(1L, "10:00"),
                new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg"),
                sequence
        );
    }
}
