package pureapps.tms.exception;

// Optionally use @ResponseStatus, but handling in @RestControllerAdvice gives more control
// @ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}