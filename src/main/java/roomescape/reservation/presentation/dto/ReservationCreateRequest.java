package roomescape.reservation.presentation.dto;

import roomescape.reservation.application.dto.ReservationCreateCommand;

public record ReservationCreateRequest(
        String name,
        String date,
        String time,
        Long timeId,
        Long themeId
) {

    public ReservationCreateCommand toCommand() {
        return new ReservationCreateCommand(name, date, time, timeId, themeId);
    }
}
