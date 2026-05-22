package roomescape.reservationwaiting.presentation.dto;

import roomescape.reservationwaiting.application.dto.ReservationWaitingCreateCommand;

public record ReservationWaitingCreateRequest(String name, String date, long timeId, long themeId) {

    public ReservationWaitingCreateCommand toCommand() {
        return new ReservationWaitingCreateCommand(name, date, timeId, themeId);
    }
}
