package roomescape.reservation.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import roomescape.reservation.application.dto.AvailableTimeQuery;
import roomescape.reservation.application.dto.ReservationCreateCommand;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.application.dto.ReservationUpdateCommand;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void findAll_success() throws Exception {
        given(reservationService.findAll())
                .willReturn(List.of(reservation()));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("브라운"));
    }

    @Test
    void create_success() throws Exception {
        given(reservationService.create(any(ReservationCreateCommand.class)))
                .willReturn(reservation());

        mockMvc.perform(post("/reservations")
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
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.time.startAt").value("10:00"));
    }

    @Test
    void update_success() throws Exception {
        given(reservationService.update(any(ReservationUpdateCommand.class)))
                .willReturn(reservation());

        mockMvc.perform(patch("/reservations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "브라운",
                                  "date": "2026-05-23",
                                  "timeId": 1,
                                  "themeId": 1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void delete_success() throws Exception {
        mockMvc.perform(delete("/reservations/1")
                        .param("name", "브라운"))
                .andExpect(status().isOk());

        verify(reservationService).delete(1L, "브라운");
    }

    @Test
    void findMine_success() throws Exception {
        given(reservationService.findMine("브라운"))
                .willReturn(List.of(reservation()));

        mockMvc.perform(get("/reservations/mine")
                        .param("name", "브라운"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("브라운"));
    }

    @Test
    void findAvailableTimes_success() throws Exception {
        given(reservationService.findAvailableTimes(any(AvailableTimeQuery.class)))
                .willReturn(List.of(time()));

        mockMvc.perform(get("/available-times")
                        .param("date", "2026-05-22")
                        .param("themeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startAt").value("10:00"));
    }

    private Reservation reservation() {
        return new Reservation(1L, "브라운", "2026-05-22", null, time(), theme());
    }

    private ReservationTime time() {
        return new ReservationTime(1L, "10:00");
    }

    private Theme theme() {
        return new Theme(1L, "잠실 미스터리", "설명", "https://example.com/theme.jpg");
    }
}
