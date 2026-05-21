package roomescape.reservationwaiting.presentation;

import roomescape.reservationwaiting.application.ReservationWaitingCreateCommand;

public record ReservationWaitingCreateRequest(String name, String date, long timeId, long themeId) {

    public ReservationWaitingCreateCommand toCommand() {
        return new ReservationWaitingCreateCommand(name, date, timeId, themeId);
    }
}
