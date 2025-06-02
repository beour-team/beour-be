package com.beour.reservation.guest.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SpaceAvailableTimeResponseDto {

    private List<LocalTime> timeList;

    @Builder
    private SpaceAvailableTimeResponseDto(List<LocalTime> timeList){
        this.timeList = timeList;
    }

    public static SpaceAvailableTimeResponseDto of(List<LocalTime> timeList){
        return SpaceAvailableTimeResponseDto.builder()
            .timeList(timeList)
            .build();
    }

}
