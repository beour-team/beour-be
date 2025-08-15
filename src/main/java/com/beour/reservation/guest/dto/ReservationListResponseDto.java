package com.beour.reservation.guest.dto;

import com.beour.reservation.commons.entity.Reservation;
import com.beour.reservation.commons.enums.ReservationStatus;
import com.beour.reservation.commons.enums.UsagePurpose;
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
    private String status;
    private String usagePurpose;
    private String requestMessage;
    private Long reviewId;
    private boolean currentUsing;

    @Builder
    private ReservationListResponseDto(Long reservationId, String spaceName,
        String spaceThumbImageUrl,
        LocalDate date, LocalTime startTime, LocalTime endTime,
        int price, int guestCount, String status,
        String usagePurpose, String requestMessage, Long reviewId, boolean currentUsing) {
        this.reservationId = reservationId;
        this.spaceName = spaceName;
        this.spaceThumbImageUrl = spaceThumbImageUrl;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.guestCount = guestCount;
        this.status = status;
        this.usagePurpose = usagePurpose;
        this.requestMessage = requestMessage;
        this.reviewId = reviewId;
        this.currentUsing = currentUsing;
    }

    public static ReservationListResponseDto of(Reservation reservation) {
        return ReservationListResponseDto.builder()
            .reservationId(reservation.getId())
            .spaceName(reservation.getSpace().getName())
            .spaceThumbImageUrl(reservation.getSpace().getThumbnailUrl())
            .date(reservation.getDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .price(reservation.getPrice())
            .guestCount(reservation.getGuestCount())
            .status(reservation.getStatus().getText())
            .usagePurpose(reservation.getUsagePurpose().getText())
            .requestMessage(reservation.getRequestMessage())
            .currentUsing(isCurrentUsing(reservation))
            .build();
    }

    public static ReservationListResponseDto of(Reservation reservation, Long reviewId) {
        return ReservationListResponseDto.builder()
            .reservationId(reservation.getId())
            .spaceName(reservation.getSpace().getName())
            .spaceThumbImageUrl(reservation.getSpace().getThumbnailUrl())
            .date(reservation.getDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .price(reservation.getPrice())
            .guestCount(reservation.getGuestCount())
            .status(reservation.getStatus().getText())
            .usagePurpose(reservation.getUsagePurpose().getText())
            .requestMessage(reservation.getRequestMessage())
            .reviewId(reviewId)
            .build();
    }

    private static boolean isCurrentUsing(Reservation reservation){
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        return reservation.getDate().equals(today) &&
            !now.isBefore(reservation.getStartTime()) &&
            now.isBefore(reservation.getEndTime());
    }
}
