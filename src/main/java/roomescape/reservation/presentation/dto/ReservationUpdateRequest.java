package roomescape.reservation.presentation.dto;

import roomescape.reservation.application.dto.ReservationUpdateCommand;

public record ReservationUpdateRequest(String name, String date, Long timeId, Long themeId) {

    public ReservationUpdateCommand toCommand(long id) {
        return new ReservationUpdateCommand(id, name, date, timeId, themeId);
    }
}
