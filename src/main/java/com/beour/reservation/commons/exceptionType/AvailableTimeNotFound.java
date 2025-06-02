package com.beour.reservation.commons.exceptionType;

public class AvailableTimeNotFound extends RuntimeException {

    public AvailableTimeNotFound(String message) {
        super(message);
    }
}
