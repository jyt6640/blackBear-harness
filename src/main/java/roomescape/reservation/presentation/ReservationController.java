package roomescape.reservation.presentation;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.application.AvailableTimeQuery;
import roomescape.reservation.application.ReservationService;
import roomescape.time.presentation.ReservationTimeResponse;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> findAll() {
        return reservationService.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/reservations")
    public ReservationResponse create(@RequestBody ReservationCreateRequest request) {
        return ReservationResponse.from(reservationService.create(request.toCommand()));
    }

    @DeleteMapping("/reservations/{id}")
    public void delete(@PathVariable long id, @RequestParam(required = false) String name) {
        reservationService.delete(id, name);
    }

    @GetMapping("/reservations/mine")
    public List<ReservationResponse> findMine(@RequestParam String name) {
        return reservationService.findMine(name).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PatchMapping("/reservations/{id}")
    public ReservationResponse update(
            @PathVariable long id,
            @RequestBody ReservationUpdateRequest request
    ) {
        return ReservationResponse.from(reservationService.update(request.toCommand(id)));
    }

    @GetMapping("/available-times")
    public List<ReservationTimeResponse> findAvailableTimes(
            @RequestParam String date,
            @RequestParam long themeId
    ) {
        return reservationService.findAvailableTimes(new AvailableTimeQuery(date, themeId)).stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }
}
