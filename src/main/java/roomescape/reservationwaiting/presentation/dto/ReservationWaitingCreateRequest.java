package roomescape.reservationwaiting.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.reservationwaiting.application.dto.ReservationWaitingCreateCommand;

public record ReservationWaitingCreateRequest(
        @NotBlank String name,
        @NotBlank String date,
        @NotNull Long timeId,
        @NotNull Long themeId
) {

    public ReservationWaitingCreateCommand toCommand() {
        return new ReservationWaitingCreateCommand(name, date, timeId, themeId);
    }
}
