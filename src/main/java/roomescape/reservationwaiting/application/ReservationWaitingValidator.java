package roomescape.reservationwaiting.application;

import org.springframework.stereotype.Component;
import roomescape.global.exception.BusinessException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservationwaiting.domain.ReservationWaitingErrorCode;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;

@Component
public class ReservationWaitingValidator {

    private final ReservationWaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;

    public ReservationWaitingValidator(
            ReservationWaitingRepository waitingRepository,
            ReservationRepository reservationRepository
    ) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
    }

    public void validateCreatable(ReservationWaitingCreateCommand command) {
        validateReservedSlot(command);
        validateNotDuplicate(command);
    }

    private void validateReservedSlot(ReservationWaitingCreateCommand command) {
        if (!reservationRepository.existsByDateAndTimeIdAndThemeId(
                command.date(),
                command.timeId(),
                command.themeId()
        )) {
            throw new BusinessException(ReservationWaitingErrorCode.WAITING_NOT_AVAILABLE);
        }
    }

    private void validateNotDuplicate(ReservationWaitingCreateCommand command) {
        if (waitingRepository.existsByScheduleAndName(
                command.date(),
                command.timeId(),
                command.themeId(),
                command.name()
        )) {
            throw new BusinessException(ReservationWaitingErrorCode.WAITING_DUPLICATE);
        }
    }
}
