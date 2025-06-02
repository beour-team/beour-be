package com.beour.reservation.guest.dto;

import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationListResponseDto {

    private Long reservationId;
    private String spaceName;
    private String spaceThumbImageUrl;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int price;
    private int guestCount;
    private ReservationStatus status;

    @Builder
    private ReservationListResponseDto(Long reservationId, String spaceName, String spaceThumbImageUrl, LocalDate date, LocalTime startTime, LocalTime endTime,
        int price, int guestCount, ReservationStatus status){
        this.reservationId = reservationId;
        this.spaceName = spaceName;
        this.spaceThumbImageUrl = spaceThumbImageUrl;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.guestCount = guestCount;
        this.status = status;
    }

    public static ReservationListResponseDto of(Reservation reservation){
        return ReservationListResponseDto.builder()
            .reservationId(reservation.getId())
            .spaceName(reservation.getSpace().getName())
            .spaceThumbImageUrl(reservation.getSpace().getThumbnailUrl())
            .date(reservation.getDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .price(reservation.getPrice())
            .guestCount(reservation.getGuestCount())
            .status(reservation.isDeleted() ? ReservationStatus.REJECTED : reservation.getStatus())
            .build();
    }

}
