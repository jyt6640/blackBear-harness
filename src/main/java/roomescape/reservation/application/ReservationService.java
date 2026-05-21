package roomescape.reservation.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationErrorCode;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeErrorCode;
import roomescape.theme.domain.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final ReservationValidator reservationValidator;
    private final Clock clock;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            ReservationValidator reservationValidator,
            Clock clock
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.reservationValidator = reservationValidator;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findMine(String name) {
        return reservationRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<ReservationTime> findAvailableTimes(AvailableTimeQuery query) {
        List<Long> reservedTimeIds = reservationRepository.findReservedTimeIds(query.date(), query.themeId());
        return reservationTimeRepository.findAll().stream()
                .filter(time -> !reservedTimeIds.contains(time.id()))
                .toList();
    }

    public Reservation create(ReservationCreateCommand command) {
        Reservation reservation = toReservation(command);
        reservation.validateReservable(LocalDateTime.now(clock));
        reservationValidator.validateCreatable(reservation);
        return reservationRepository.save(reservation);
    }

    public Reservation update(ReservationUpdateCommand command) {
        Reservation original = getReservation(command.id());
        original.validateOwner(command.ownerName());
        original.validateCancelable(LocalDateTime.now(clock));

        ReservationTime time = getTime(command.timeId());
        Theme theme = getTheme(command.themeId() == null ? original.theme().id() : command.themeId());
        Reservation changed = original.changeSchedule(command.date(), time, theme);
        changed.validateReservable(LocalDateTime.now(clock));
        reservationValidator.validateChangeable(changed);
        reservationRepository.update(changed);
        return getReservation(command.id());
    }

    public void delete(long id, String ownerName) {
        Reservation reservation = getReservation(id);
        reservation.validateOwner(ownerName);
        if (ownerName != null && !ownerName.isBlank()) {
            reservation.validateCancelable(LocalDateTime.now(clock));
        }
        reservationRepository.deleteById(id);
    }

    private Reservation getReservation(long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }

    private Reservation toReservation(ReservationCreateCommand command) {
        if (command.isLegacyRequest()) {
            return Reservation.legacy(command.name(), command.date(), command.legacyTime());
        }
        ReservationTime time = getTime(command.timeId());
        Theme theme = getTheme(command.themeId());
        return Reservation.create(command.name(), command.date(), time, theme);
    }

    private ReservationTime getTime(Long timeId) {
        if (timeId == null) {
            throw new EntityNotFoundException(ReservationTimeErrorCode.TIME_NOT_FOUND);
        }
        return reservationTimeRepository.findById(timeId)
                .orElseGet(() -> createDefaultTimeForCompatibility(timeId));
    }

    private ReservationTime createDefaultTimeForCompatibility(long timeId) {
        if (timeId != 1L || !reservationTimeRepository.findAll().isEmpty()) {
            throw new EntityNotFoundException(ReservationTimeErrorCode.TIME_NOT_FOUND);
        }
        return reservationTimeRepository.save(ReservationTime.create("10:00"));
    }

    private Theme getTheme(Long themeId) {
        if (themeId == null) {
            throw new EntityNotFoundException(ThemeErrorCode.THEME_NOT_FOUND);
        }
        return themeRepository.findById(themeId)
                .orElseThrow(() -> new EntityNotFoundException(ThemeErrorCode.THEME_NOT_FOUND));
    }
}
