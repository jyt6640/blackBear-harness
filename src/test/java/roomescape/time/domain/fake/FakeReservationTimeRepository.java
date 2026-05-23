package roomescape.time.domain.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import roomescape.time.domain.ReservationTime;
import roomescape.time.domain.ReservationTimeRepository;

public class FakeReservationTimeRepository implements ReservationTimeRepository {

    private final List<ReservationTime> times = new ArrayList<>();

    @Override
    public List<ReservationTime> findAll() {
        return times;
    }

    @Override
    public Optional<ReservationTime> findById(long id) {
        return times.stream()
                .filter(time -> time.id().equals(id))
                .findFirst();
    }

    @Override
    public ReservationTime save(ReservationTime reservationTime) {
        ReservationTime saved = ReservationTime.restore((long) times.size() + 1, reservationTime.startAt());
        times.add(saved);
        return saved;
    }

    @Override
    public void deleteById(long id) {
        times.removeIf(time -> time.id().equals(id));
    }
}
