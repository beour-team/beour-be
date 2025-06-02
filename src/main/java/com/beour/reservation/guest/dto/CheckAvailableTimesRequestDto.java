package com.beour.reservation.guest.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailableTimesRequestDto {

    @NotNull(message = "공간 id 입력은 필수입니다.")
    private Long spaceId;

    @NotNull(message = "날짜 입력은 필수입니다.")
    private LocalDate date;
}
