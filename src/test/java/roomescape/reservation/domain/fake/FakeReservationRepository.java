package roomescape.reservation.domain.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;

public class FakeReservationRepository implements ReservationRepository {

    private final List<Reservation> reservations = new ArrayList<>();
    private final List<Long> deletedIds = new ArrayList<>();
    private boolean timeInUse;
    private boolean themeInUse;
    private boolean reservedSlotExists;

    public void add(Reservation reservation) {
        reservations.add(reservation);
    }

    public List<Reservation> reservations() {
        return reservations;
    }

    public List<Long> deletedIds() {
        return deletedIds;
    }

    public void markTimeInUse() {
        timeInUse = true;
    }

    public void markThemeInUse() {
        themeInUse = true;
    }

    public void markReservedSlotExists() {
        reservedSlotExists = true;
    }

    @Override
    public List<Reservation> findAll() {
        return reservations;
    }

    @Override
    public List<Reservation> findByName(String name) {
        return reservations.stream()
                .filter(reservation -> reservation.name().equals(name))
                .toList();
    }

    @Override
    public Optional<Reservation> findById(long id) {
        return reservations.stream()
                .filter(reservation -> reservation.id().equals(id))
                .findFirst();
    }

    @Override
    public Reservation save(Reservation reservation) {
        Reservation saved = Reservation.restore(
                (long) reservations.size() + 1,
                reservation.name(),
                reservation.date(),
                reservation.legacyTime(),
                reservation.time(),
                reservation.theme()
        );
        reservations.add(saved);
        return saved;
    }

    @Override
    public void update(Reservation reservation) {
        deleteById(reservation.id());
        reservations.add(reservation);
    }

    @Override
    public void deleteById(long id) {
        deletedIds.add(id);
        reservations.removeIf(reservation -> reservation.id().equals(id));
    }

    @Override
    public boolean existsByTimeId(long timeId) {
        return timeInUse || reservations.stream()
                .filter(Reservation::hasStructuredSchedule)
                .anyMatch(reservation -> reservation.time().id().equals(timeId));
    }

    @Override
    public boolean existsByThemeId(long themeId) {
        return themeInUse || reservations.stream()
                .filter(Reservation::hasStructuredSchedule)
                .anyMatch(reservation -> reservation.theme().id().equals(themeId));
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeId(String date, long timeId, long themeId) {
        return reservedSlotExists || reservations.stream()
                .filter(Reservation::hasStructuredSchedule)
                .anyMatch(reservation -> hasSameSchedule(reservation, date, timeId, themeId));
    }

    @Override
    public boolean existsByDateAndTimeIdAndThemeIdExcept(String date, long timeId, long themeId, long reservationId) {
        return reservations.stream()
                .filter(Reservation::hasStructuredSchedule)
                .filter(reservation -> !reservation.id().equals(reservationId))
                .anyMatch(reservation -> hasSameSchedule(reservation, date, timeId, themeId));
    }

    @Override
    public List<Long> findReservedTimeIds(String date, long themeId) {
        return reservations.stream()
                .filter(Reservation::hasStructuredSchedule)
                .filter(reservation -> reservation.date().equals(date))
                .filter(reservation -> reservation.theme().id().equals(themeId))
                .map(reservation -> reservation.time().id())
                .toList();
    }

    private boolean hasSameSchedule(Reservation reservation, String date, long timeId, long themeId) {
        return reservation.date().equals(date)
                && reservation.time().id().equals(timeId)
                && reservation.theme().id().equals(themeId);
    }
}
