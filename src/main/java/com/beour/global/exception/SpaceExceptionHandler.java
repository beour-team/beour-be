package com.beour.global.exception;

import com.beour.global.exception.exceptionType.InvalidCategoryException;
import com.beour.global.exception.exceptionType.SpaceNotFoundException;
import com.beour.global.response.ErrorResponse;
import com.beour.global.exception.exceptionType.AvailableTimeNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SpaceExceptionHandler {
    @ExceptionHandler(SpaceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSpaceNotFound(SpaceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getErrorCode(), "SPACE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(AvailableTimeNotFound.class)
    public ResponseEntity<ErrorResponse> availableTimeNotFound(AvailableTimeNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "AVAILABLE_TIME_NOT_FOUND",
                ex.getMessage()));
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCategory(InvalidCategoryException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getErrorCode(), ex.getCodeName(), ex.getMessage()));
    }

}
