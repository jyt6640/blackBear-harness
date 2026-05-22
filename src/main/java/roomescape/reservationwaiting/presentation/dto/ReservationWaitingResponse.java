package roomescape.reservationwaiting.presentation.dto;

import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.theme.presentation.dto.ThemeResponse;
import roomescape.time.presentation.dto.ReservationTimeResponse;

public record ReservationWaitingResponse(
        Long id,
        String name,
        String date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        int sequence
) {

    public static ReservationWaitingResponse from(ReservationWaiting waiting) {
        return new ReservationWaitingResponse(
                waiting.id(),
                waiting.name(),
                waiting.date(),
                ReservationTimeResponse.from(waiting.time()),
                ThemeResponse.from(waiting.theme()),
                waiting.sequence()
        );
    }
}
