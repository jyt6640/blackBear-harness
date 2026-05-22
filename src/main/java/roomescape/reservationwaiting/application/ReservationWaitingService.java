package roomescape.reservationwaiting.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservationwaiting.application.dto.ReservationWaitingCreateCommand;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

@Service
@Transactional
public class ReservationWaitingService {

    private final ReservationWaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository timeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationWaitingValidator validator;

    public ReservationWaitingService(
            ReservationWaitingRepository waitingRepository,
            ReservationRepository reservationRepository,
            ReservationTimeRepository timeRepository,
            ThemeRepository themeRepository,
            ReservationWaitingValidator validator
    ) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.timeRepository = timeRepository;
        this.themeRepository = themeRepository;
        this.validator = validator;
    }

    public ReservationWaiting create(ReservationWaitingCreateCommand command) {
        validator.validateCreatable(command);
        ReservationTime time = getTime(command.timeId());
        Theme theme = getTheme(command.themeId());
        int sequence = waitingRepository.countBySchedule(command.date(), command.timeId(), command.themeId()) + 1;
        return waitingRepository.save(ReservationWaiting.create(
                command.name(),
                command.date(),
                time,
                theme,
                sequence
        ));
    }

    @Transactional(readOnly = true)
    public List<ReservationWaiting> findBySchedule(String date, long timeId, long themeId) {
        return waitingRepository.findBySchedule(date, timeId, themeId);
    }

    private ReservationTime getTime(long timeId) {
        return timeRepository.findById(timeId)
                .orElseThrow(() -> new EntityNotFoundException(ReservationTimeErrorCode.TIME_NOT_FOUND));
    }

    private Theme getTheme(long themeId) {
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new EntityNotFoundException(ThemeErrorCode.THEME_NOT_FOUND));
    }
}
