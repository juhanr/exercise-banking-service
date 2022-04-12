package ee.juhanr.exercise.banking.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFound extends ServiceException {

    public ResourceNotFound(String message) {
        super(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", message);
    }
}
