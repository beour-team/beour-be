package com.beour.review.guest.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewableSpaceDto {
    private Long reservationId;
    private String spaceName;
    private String date;
    private String startTime;
    private String endTime;
    private int guestCount;
    private String thumbnailUrl;
}
