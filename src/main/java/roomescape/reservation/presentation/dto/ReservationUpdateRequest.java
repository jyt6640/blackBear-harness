package roomescape.reservation.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import roomescape.reservation.application.dto.ReservationUpdateCommand;

public record ReservationUpdateRequest(@NotBlank String name, @NotBlank String date, Long timeId, Long themeId) {

    public ReservationUpdateCommand toCommand(long id) {
        return new ReservationUpdateCommand(id, name, date, timeId, themeId);
    }
}
