package roomescape.reservationwaiting.application;

public record ReservationWaitingCreateCommand(String name, String date, long timeId, long themeId) {
}
