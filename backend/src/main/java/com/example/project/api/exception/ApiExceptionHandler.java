package com.example.project.api.exception;

import com.example.project.api.dto.common.ErrorResponse;
import com.example.project.application.exception.ApplicationConfigurationException;
import com.example.project.application.exception.ApplicationException;
import com.example.project.application.exception.BadRequestApplicationException;
import com.example.project.application.exception.ForbiddenApplicationException;
import com.example.project.application.exception.ResourceNotFoundApplicationException;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed.", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed.", List.of(ex.getMessage()));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplication(ApplicationException ex) {
        HttpStatus status = resolveStatus(ex);
        return build(status, ex.getErrorCode(), ex.getMessage(), List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Unexpected server error.", List.of());
    }

    private HttpStatus resolveStatus(ApplicationException ex) {
        if (ex instanceof BadRequestApplicationException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (ex instanceof ForbiddenApplicationException) {
            return HttpStatus.FORBIDDEN;
        }
        if (ex instanceof ResourceNotFoundApplicationException) {
            return HttpStatus.NOT_FOUND;
        }
        if (ex instanceof ApplicationConfigurationException) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, List<String> details) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, details, OffsetDateTime.now()));
    }
}
