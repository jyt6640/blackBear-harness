package roomescape.reservation.application;

public record ReservationUpdateCommand(
        long id,
        String ownerName,
        String date,
        Long timeId,
        Long themeId
) {
}
