package com.beour.reservation.guest.dto;

import com.beour.reservation.commons.entity.Reservation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationResponseDto {

    private Long id;

    @Builder
    private ReservationResponseDto(Long id){
        this.id = id;
    }

    public static ReservationResponseDto of(Reservation reservation){
        return ReservationResponseDto.builder()
            .id(reservation.getId())
            .build();
    }
}
