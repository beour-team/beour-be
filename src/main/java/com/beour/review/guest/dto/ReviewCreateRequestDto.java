package com.beour.review.guest.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewCreateRequestDto {
    private Long reservationId;
    private int rating;
    private String content;
    private List<String> imageUrls;
}