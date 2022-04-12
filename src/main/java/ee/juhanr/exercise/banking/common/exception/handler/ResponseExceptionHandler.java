package ee.juhanr.exercise.banking.common.exception.handler;

import ee.juhanr.exercise.banking.common.SessionContext;
import ee.juhanr.exercise.banking.common.exception.ServiceException;
import ee.juhanr.exercise.banking.common.exception.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ResponseExceptionHandler {

    private final SessionContext sessionContext;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .sorted()
                .map(v -> String.format("field %s %s", v.getField(), v.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        return newResponseEntity(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }

    @ExceptionHandler(ServiceException.class)
    ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        return newResponseEntity(e.getHttpStatus(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unknown exception:", e);
        return newResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", e.getMessage());
    }

    private ResponseEntity<ErrorResponse> newResponseEntity(HttpStatus httpStatus, String code, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse
                        .builder()
                        .code(code)
                        .message(message)
                        .sessionId(sessionContext.getSessionId())
                        .build());
    }

}
