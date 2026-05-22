package roomescape.time.application;

import org.springframework.stereotype.Component;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

@Component
public class ReservationTimeValidator {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeValidator(
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public void validateRemovable(long id) {
        validateExists(id);
        validateNotUsed(id);
    }

    private void validateExists(long id) {
        reservationTimeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ReservationTimeErrorCode.TIME_NOT_FOUND));
    }

    private void validateNotUsed(long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new BusinessException(ReservationTimeErrorCode.TIME_IN_USE);
        }
    }
}
