package com.beour.reservation.guest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateRequest {

    @NotNull(message = "예약자의 id 값 필수")
    private Long guestId;

    @NotNull(message = "호스트의 id 값 필수")
    private Long hostId;

    @NotNull(message = "예약할 공간 id 값 필수")
    private Long spaceId;

    @NotNull(message = "예약일자 필수")
    private LocalDate date;

    @NotNull(message = "이용 시작 시간 필수")
    private LocalTime startTime;

    @NotNull(message = "이용 마감 시간 필수")
    private LocalTime endTime;

    @Min(0)
    private int price;

    @Min(1)
    private int guestCount;

}
