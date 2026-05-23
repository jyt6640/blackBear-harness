package roomescape.reservation.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import roomescape.reservation.application.dto.ReservationCreateCommand;

public record ReservationCreateRequest(
        @NotBlank String name,
        @NotBlank String date,
        String time,
        Long timeId,
        Long themeId
) {

    public ReservationCreateCommand toCommand() {
        return new ReservationCreateCommand(name, date, time, timeId, themeId);
    }
}
