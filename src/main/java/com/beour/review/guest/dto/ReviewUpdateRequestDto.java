package com.beour.review.guest.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ReviewUpdateRequestDto {
    private int rating;
    private String content;
    private List<String> imageUrls;
}