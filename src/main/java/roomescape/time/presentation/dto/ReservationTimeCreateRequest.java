package roomescape.time.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ReservationTimeCreateRequest(@NotBlank String startAt) {
}
