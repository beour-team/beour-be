package com.beour.reservation.host.dto;

import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class CalendarReservationResponseDto {

    private Long reservationId;
    private String guestName;
    private String status;
    private String spaceName;
    private LocalTime startTime;
    private LocalTime endTime;
    private int guestCount;

    @Builder
    private CalendarReservationResponseDto(Long reservationId, String guestName, String status,
                                           String spaceName, LocalTime startTime, LocalTime endTime, int guestCount) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.status = status;
        this.spaceName = spaceName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.guestCount = guestCount;
    }

    public static CalendarReservationResponseDto of(Reservation reservation) {
        return CalendarReservationResponseDto.builder()
                .reservationId(reservation.getId())
                .guestName(reservation.getGuest().getName())
                .status(reservation.getStatus().getText())
                .spaceName(reservation.getSpace().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .guestCount(reservation.getGuestCount())
                .build();
    }
}
