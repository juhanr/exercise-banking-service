package ee.juhanr.exercise.banking.common.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final String code;
    private final String message;
    private final String sessionId;
}
