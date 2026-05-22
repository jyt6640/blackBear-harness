package roomescape.reservationwaiting.application.dto;

public record ReservationWaitingCreateCommand(String name, String date, long timeId, long themeId) {
}
