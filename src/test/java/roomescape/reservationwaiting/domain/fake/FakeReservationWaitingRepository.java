package roomescape.reservationwaiting.domain.fake;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import roomescape.reservationwaiting.domain.ReservationWaiting;
import roomescape.reservationwaiting.domain.ReservationWaitingRepository;

public class FakeReservationWaitingRepository implements ReservationWaitingRepository {

    private final List<ReservationWaiting> waitings = new ArrayList<>();
    private final List<Long> deletedIds = new ArrayList<>();
    private boolean advanced;

    public void add(ReservationWaiting waiting) {
        waitings.add(waiting);
    }

    public List<ReservationWaiting> waitings() {
        return waitings;
    }

    public List<Long> deletedIds() {
        return deletedIds;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public List<ReservationWaiting> findBySchedule(String date, long timeId, long themeId) {
        return waitings.stream()
                .filter(waiting -> hasSameSchedule(waiting, date, timeId, themeId))
                .toList();
    }

    @Override
    public Optional<ReservationWaiting> findFirstBySchedule(String date, long timeId, long themeId) {
        return findBySchedule(date, timeId, themeId).stream()
                .min(Comparator.comparingInt(ReservationWaiting::sequence));
    }

    @Override
    public ReservationWaiting save(ReservationWaiting waiting) {
        ReservationWaiting saved = ReservationWaiting.restore(
                (long) waitings.size() + 1,
                waiting.name(),
                waiting.date(),
                waiting.time(),
                waiting.theme(),
                waiting.sequence()
        );
        waitings.add(saved);
        return saved;
    }

    @Override
    public void deleteById(long id) {
        deletedIds.add(id);
        waitings.removeIf(waiting -> waiting.id().equals(id));
    }

    @Override
    public void advanceSequences(String date, long timeId, long themeId, int deletedSequence) {
        advanced = true;
    }

    @Override
    public boolean existsByScheduleAndName(String date, long timeId, long themeId, String name) {
        return waitings.stream()
                .filter(waiting -> hasSameSchedule(waiting, date, timeId, themeId))
                .anyMatch(waiting -> waiting.name().equals(name));
    }

    @Override
    public int countBySchedule(String date, long timeId, long themeId) {
        return findBySchedule(date, timeId, themeId).size();
    }

    private boolean hasSameSchedule(ReservationWaiting waiting, String date, long timeId, long themeId) {
        return waiting.date().equals(date)
                && waiting.time().id().equals(timeId)
                && waiting.theme().id().equals(themeId);
    }
}
