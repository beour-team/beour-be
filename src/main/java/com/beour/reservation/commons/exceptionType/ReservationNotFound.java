package com.beour.reservation.commons.exceptionType;

public class ReservationNotFound extends RuntimeException{

    public ReservationNotFound(String message) {
        super(message);
    }

}
