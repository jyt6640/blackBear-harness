package roomescape.global.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleRoomEscapeException_success() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleRoomEscapeException(
                new BadRequestException(CommonErrorCode.INVALID_REQUEST)
        );

        assertThat(response.getStatusCode()).isEqualTo(CommonErrorCode.INVALID_REQUEST.status());
        assertThat(response.getBody()).isEqualTo(ErrorResponse.from(CommonErrorCode.INVALID_REQUEST));
    }

    @Test
    void handleBadRequest_success() {
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(new IllegalArgumentException());

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo(ErrorResponse.from(CommonErrorCode.INVALID_REQUEST));
    }
}
