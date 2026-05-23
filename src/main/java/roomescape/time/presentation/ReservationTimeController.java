package roomescape.time.presentation;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.time.application.ReservationTimeService;
import roomescape.time.presentation.dto.ReservationTimeCreateRequest;
import roomescape.time.presentation.dto.ReservationTimeResponse;

@RestController
public class ReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    public ReservationTimeController(ReservationTimeService reservationTimeService) {
        this.reservationTimeService = reservationTimeService;
    }

    @GetMapping("/times")
    public List<ReservationTimeResponse> findAll() {
        return reservationTimeService.findAll().stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    @PostMapping("/times")
    public ReservationTimeResponse create(@Valid @RequestBody ReservationTimeCreateRequest request) {
        return ReservationTimeResponse.from(reservationTimeService.create(request.startAt()));
    }

    @DeleteMapping("/times/{id}")
    public void delete(@PathVariable long id) {
        reservationTimeService.delete(id);
    }
}
