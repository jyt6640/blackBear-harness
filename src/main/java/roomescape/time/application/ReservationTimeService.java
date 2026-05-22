package roomescape.time.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.EntityNotFoundException;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeErrorCode;
import roomescape.time.domain.ReservationTimeRepository;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationTimeValidator reservationTimeValidator;

    public ReservationTimeService(
            ReservationTimeRepository reservationTimeRepository,
            ReservationTimeValidator reservationTimeValidator
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationTimeValidator = reservationTimeValidator;
    }

    @Transactional(readOnly = true)
    public List<ReservationTime> findAll() {
        return reservationTimeRepository.findAll();
    }

    public ReservationTime create(String startAt) {
        return reservationTimeRepository.save(ReservationTime.create(startAt));
    }

    public void delete(long id) {
        reservationTimeValidator.validateRemovable(id);
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

}
