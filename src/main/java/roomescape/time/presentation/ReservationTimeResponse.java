package roomescape.time.presentation;

import roomescape.time.domain.ReservationTime;

public record ReservationTimeResponse(Long id, String startAt) {

    public static ReservationTimeResponse from(ReservationTime reservationTime) {
        return new ReservationTimeResponse(reservationTime.id(), reservationTime.startAt());
    }
}
