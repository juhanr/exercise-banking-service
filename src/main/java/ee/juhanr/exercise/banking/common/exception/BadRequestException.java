package ee.juhanr.exercise.banking.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ServiceException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", message);
    }
}
