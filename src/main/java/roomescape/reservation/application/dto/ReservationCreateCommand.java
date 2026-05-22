package roomescape.reservation.application.dto;

public record ReservationCreateCommand(
        String name,
        String date,
        String legacyTime,
        Long timeId,
        Long themeId
) {

    public boolean isLegacyRequest() {
        return timeId == null && themeId == null;
    }
}
