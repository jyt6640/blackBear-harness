package roomescape.reservation.presentation;

import roomescape.reservation.application.ReservationCreateCommand;

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
