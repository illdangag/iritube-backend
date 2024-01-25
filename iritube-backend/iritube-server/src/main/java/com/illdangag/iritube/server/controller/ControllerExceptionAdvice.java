package com.illdangag.iritube.server.controller;

import com.illdangag.iritube.core.data.response.ErrorResponse;
import com.illdangag.iritube.core.exception.IritubeCoreError;
import com.illdangag.iritube.core.exception.IritubeException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Set;

@Slf4j
@RestControllerAdvice("com.illdangag.iritube.server.controller")
public class ControllerExceptionAdvice {

    /**
     * 예상한 예외
     */
    @ExceptionHandler({IritubeException.class})
    public ResponseEntity<ErrorResponse> throwIritubeException(IritubeException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(exception.getErrorResponse());
    }

    /**
     * 요청에 대한 유효성 검사 실패
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = null;
        if (exception.getFieldError() != null && exception.getFieldError().getDefaultMessage() != null) {
            message = exception.getFieldError().getDefaultMessage();
        } else {
            message = IritubeCoreError.INVALID_REQUEST.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(IritubeCoreError.INVALID_REQUEST.getCode())
                .message(message).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * 요청에 대한 유효성 검사 실패
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException exception) {
        String message = null;
        Set<ConstraintViolation<?>> set = exception.getConstraintViolations();
        if (set != null && !set.isEmpty()) {
            ConstraintViolation<?>[] violations = set.toArray(new ConstraintViolation[0]);
            message = violations[0].getMessage();
        } else {
            message = IritubeCoreError.INVALID_REQUEST.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(IritubeCoreError.INVALID_REQUEST.getCode())
                .message(message).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }

    /**
     * 예상하지 못한 예외
     */
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse> unknownException(Exception exception) {
        log.error("Unknown Exception", exception);
        ErrorResponse errorResponse = ErrorResponse.builder().build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}