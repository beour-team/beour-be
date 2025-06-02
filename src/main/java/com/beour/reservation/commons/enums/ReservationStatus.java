package com.beour.reservation.commons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    PENDING("예약 대기 상태"),
    ACCEPTED("예약 완료 상태"),
    REJECTED("예약 거부 상태 + 예약 취소 상태"),
    COMPLETED("사용 완료");

    private final String text;
    public static final ReservationStatus DEFAULT = PENDING;

}
