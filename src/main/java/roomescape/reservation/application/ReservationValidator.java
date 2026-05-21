package roomescape.reservation.application;

import org.springframework.stereotype.Component;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationErrorCode;
import roomescape.reservation.domain.ReservationRepository;

@Component
public class ReservationValidator {

    private final ReservationRepository reservationRepository;

    public ReservationValidator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void validateCreatable(Reservation reservation) {
        if (!reservation.hasStructuredSchedule()) {
            return;
        }
        if (reservationRepository.existsByDateAndTimeIdAndThemeId(
                reservation.date(),
                reservation.time().id(),
                reservation.theme().id()
        )) {
            throw new BusinessException(ReservationErrorCode.DUPLICATE_RESERVATION);
        }
    }

    public void validateChangeable(Reservation reservation) {
        if (!reservation.hasStructuredSchedule()) {
            return;
        }
        if (reservationRepository.existsByDateAndTimeIdAndThemeIdExcept(
                reservation.date(),
                reservation.time().id(),
                reservation.theme().id(),
                reservation.id()
        )) {
            throw new BusinessException(ReservationErrorCode.DUPLICATE_RESERVATION);
        }
    }
}
