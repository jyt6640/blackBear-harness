package roomescape.time.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.global.exception.BadRequestException;

class ReservationTimeTest {

    @Test
    void create_success() {
        ReservationTime reservationTime = ReservationTime.create("10:00");

        assertThat(reservationTime.startAt()).isEqualTo("10:00");
    }

    @Test
    void create_fail_with_blank_startAt() {
        assertThatThrownBy(() -> ReservationTime.create(" "))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.INVALID_START_AT);
    }

    @Test
    void create_fail_with_invalid_startAt() {
        assertThatThrownBy(() -> ReservationTime.create("10시"))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode")
                .isEqualTo(ReservationTimeErrorCode.INVALID_START_AT);
    }
}
