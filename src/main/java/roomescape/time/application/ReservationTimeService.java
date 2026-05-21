package roomescape.time.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BusinessException;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository,
            ReservationRepository reservationRepository
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationTime> findAll() {
        return reservationTimeRepository.findAll();
    }

    public ReservationTime create(String startAt) {
        return reservationTimeRepository.save(ReservationTime.create(startAt));
    }

    public void delete(long id) {
        validateExists(id);
        validateNotUsed(id);
        reservationTimeRepository.deleteById(id);
    }

    public ReservationTime getOrCreateDefaultTime(long id) {
        return reservationTimeRepository.findById(id)
                .orElseGet(() -> createDefaultTimeForCompatibility(id));
    }

    private ReservationTime createDefaultTimeForCompatibility(long id) {
        if (id != 1L || !reservationTimeRepository.findAll().isEmpty()) {
            throw new EntityNotFoundException(ReservationTimeErrorCode.TIME_NOT_FOUND);
        }
        return reservationTimeRepository.save(ReservationTime.create("10:00"));
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
