package roomescape.reservation.domain;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    List<Reservation> findAll();

    List<Reservation> findByName(String name);

    Optional<Reservation> findById(long id);

    Reservation save(Reservation reservation);

    void update(Reservation reservation);

    void deleteById(long id);

    boolean existsByTimeId(long timeId);

    boolean existsByThemeId(long themeId);

    boolean existsByDateAndTimeIdAndThemeId(String date, long timeId, long themeId);

    boolean existsByDateAndTimeIdAndThemeIdExcept(String date, long timeId, long themeId, long reservationId);

    List<Long> findReservedTimeIds(String date, long themeId);
}
