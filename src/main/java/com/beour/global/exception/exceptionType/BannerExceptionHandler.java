package com.beour.global.exception.exceptionType;

import com.beour.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BannerExceptionHandler {

    @ExceptionHandler(BannerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(BannerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                new ErrorResponse(ex.getErrorCode(), "BANNER_NOT_FOUND", ex.getMessage()));
    }

}
