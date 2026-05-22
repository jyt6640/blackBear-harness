package roomescape.reservation.application.dto;

public record ReservationUpdateCommand(
        long id,
        String ownerName,
        String date,
        Long timeId,
        Long themeId
) {
}
