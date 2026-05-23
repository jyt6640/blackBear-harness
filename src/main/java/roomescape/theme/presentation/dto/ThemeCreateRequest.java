package roomescape.theme.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record ThemeCreateRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String thumbnailUrl
) {
}
