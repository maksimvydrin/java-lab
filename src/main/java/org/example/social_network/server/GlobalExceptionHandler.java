package org.example.social_network.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) { super(message); }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { super(message); }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) { super(message); }
    }

    public static class NotEditableException extends RuntimeException {
        public NotEditableException(String message) { super(message); }
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidation(ValidationException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return problem(HttpStatus.CONFLICT, "Conflict", ex.getMessage());
    }

    @ExceptionHandler(NotEditableException.class)
    public ProblemDetail handleNotEditable(NotEditableException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Entity Not Editable", ex.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ProblemDetail handleIo(IOException ex) {

        return problem(HttpStatus.BAD_REQUEST, "Serialization / Migration Error", ex.getMessage());
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title);
        pd.setType(URI.create("https://example.org/errors/" + status.value()));
        return pd;
    }
}