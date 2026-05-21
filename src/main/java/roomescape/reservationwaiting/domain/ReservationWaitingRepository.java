package roomescape.reservationwaiting.domain;

import java.util.List;
import java.util.Optional;

public interface ReservationWaitingRepository {

    List<ReservationWaiting> findBySchedule(String date, long timeId, long themeId);

    Optional<ReservationWaiting> findFirstBySchedule(String date, long timeId, long themeId);

    ReservationWaiting save(ReservationWaiting waiting);

    void deleteById(long id);

    void advanceSequences(String date, long timeId, long themeId, int deletedSequence);

    boolean existsByScheduleAndName(String date, long timeId, long themeId, String name);

    int countBySchedule(String date, long timeId, long themeId);
}
