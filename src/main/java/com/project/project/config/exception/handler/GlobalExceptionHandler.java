package com.project.project.config.exception.handler;

import com.project.project.config.exception.DuplicatedException;
import com.project.project.config.exception.NotFoundDataException;
import com.project.project.config.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<String> errorMessage = fieldErrors.stream()
                .map(error -> error.getField() + ", " + error.getDefaultMessage())
                .toList();
        errorMessage.forEach(s -> log.error("Validation failed: " + s));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionMessage(errorMessage));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionMessage> handlerDataIntegrityViolationException(DataIntegrityViolationException ex) {
        List<String> errorMessage = List.of("Invalid input");
        log.error("SQL integrity constraint violation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionMessage(errorMessage));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<ExceptionMessage> handlerServerException(ServerException ex) {
        List<String> errorMessage = List.of("An error occurred");
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionMessage(errorMessage));
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<ExceptionMessage> handlerNotFoundMemberException(NotFoundDataException ex) {
        List<String> errorMessage = List.of(ex.getMessage());
        errorMessage.forEach(log::error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionMessage(errorMessage));
    }

    @ExceptionHandler(DuplicatedException.class)
    public ResponseEntity<ExceptionMessage> handlerDuplicatedException(DuplicatedException ex) {
        List<String> errorMessage = List.of(ex.getMessage());
        errorMessage.forEach(log::error);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ExceptionMessage(errorMessage));
    }
}
