package roomescape.reservation.presentation.dto;

import roomescape.reservation.domain.Reservation;
import roomescape.theme.presentation.dto.ThemeResponse;
import roomescape.time.presentation.dto.ReservationTimeResponse;

public record ReservationResponse(
        Long id,
        String name,
        String date,
        Object time,
        ThemeResponse theme
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.id(),
                reservation.name(),
                reservation.date(),
                timeResponse(reservation),
                ThemeResponse.from(reservation.theme())
        );
    }

    private static Object timeResponse(Reservation reservation) {
        if (reservation.time() == null) {
            return reservation.legacyTime();
        }
        return ReservationTimeResponse.from(reservation.time());
    }
}
