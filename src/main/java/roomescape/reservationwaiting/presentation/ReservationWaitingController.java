package roomescape.reservationwaiting.presentation;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservationwaiting.application.ReservationWaitingService;
import roomescape.reservationwaiting.presentation.dto.ReservationWaitingCreateRequest;
import roomescape.reservationwaiting.presentation.dto.ReservationWaitingResponse;

@RestController
public class ReservationWaitingController {

    private final ReservationWaitingService service;

    public ReservationWaitingController(ReservationWaitingService service) {
        this.service = service;
    }

    @PostMapping("/reservation-waitings")
    public ReservationWaitingResponse create(@Valid @RequestBody ReservationWaitingCreateRequest request) {
        return ReservationWaitingResponse.from(service.create(request.toCommand()));
    }

    @GetMapping("/reservation-waitings")
    public List<ReservationWaitingResponse> findBySchedule(
            @RequestParam String date,
            @RequestParam long timeId,
            @RequestParam long themeId
    ) {
        return service.findBySchedule(date, timeId, themeId).stream()
                .map(ReservationWaitingResponse::from)
                .toList();
    }
}
