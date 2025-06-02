package com.beour.global.exception;

import com.beour.global.response.ErrorResponse;
import com.beour.reservation.commons.exceptionType.ReservationNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReservationExceptionHandler {

    @ExceptionHandler(ReservationNotFound.class)
    public ResponseEntity<ErrorResponse> reservationNotFound(ReservationNotFound ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "RESERVATION_NOT_FOUND", ex.getMessage()));
    }

}
